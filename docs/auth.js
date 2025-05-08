import { backendURL, privateKey, loginApi, signupApi } from "./config.js";

document.addEventListener('DOMContentLoaded', () => {
    const userData = localStorage.getItem('userData');
    if (userData) {
        window.location.replace('index.html');
    }
});

const authForm = document.getElementById('auth-form');
const authTitle = document.getElementById('auth-title');
const authSubTitle = document.getElementById('auth-subtitle');
const authButton = document.getElementById('auth-button');
const toggleAuthLink = document.getElementById('toggle-auth-link');
const errorMessage = document.getElementById('error-message');

let isLoginMode = true;

toggleAuthLink.addEventListener('click', (e) => {
    e.preventDefault();
    isLoginMode = !isLoginMode;
    authTitle.textContent = isLoginMode ? 'Login' : 'Sign Up';
    authButton.textContent = isLoginMode ? 'Login' : 'Sign Up';
    authSubTitle.textContent = isLoginMode ? 'Please login to your account' : 'Please signup here';
    toggleAuthLink.textContent = isLoginMode ? 
        "Don't have an account? Sign up" : 
        "Already have an account? Login";
});

authForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const endpoint = isLoginMode ? `${loginApi}` : `${signupApi}`;
        const jwtToken = await generateJWT(email, password, isLoginMode ? 'user' : 'admin');
        const response = await fetch(`${backendURL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            }
        });

        console.log("after api")

        if (!response.ok) {

            const errorData = await response.json();
            console.error('Full error response:', errorData);
            let errorMessage = errorData.message || 'Authentication failed';
            
            throw new Error(errorMessage);
        }

        const data = await response.json();
        
        localStorage.setItem('userData', JSON.stringify(data));
        window.location.replace('index.html');

    } catch (error) {
        showError(error.message, error.status);
    }
});

async function generateJWT(email, password, role) {
    const header = {
        alg: 'RS256',
        typ: 'JWT'
    };

    const payload = {
        email: email,
        password: password,
        role: role,
        iat: Math.floor(Date.now() / 1000),
        exp: Math.floor(Date.now() / 1000) + (60 * 60) 
    };

    try {
        const sHeader = JSON.stringify(header);
        const sPayload = JSON.stringify(payload);
        const sJWT = KJUR.jws.JWS.sign("RS256", sHeader, sPayload, privateKey);
        return sJWT;
    } catch (error) {
        console.error("Error generating JWT:", error);
        throw new Error("Error generating JWT:");
    }
}

function decodeJWT(token) {
    try {
        const decoded = KJUR.jws.JWS.parse(token);
        return decoded.payloadObj;
    } catch (error) {
        console.error('Error decoding JWT:', error);
        return null;
    }
}

function showError(error, statusCode) {
    let errorMessageText;
    
    switch(statusCode) {
        case 400:
            errorMessageText = 'Invalid request. Please check your input.';
            break;
        case 401:
            errorMessageText = 'Invalid credentials. Please try again.';
            break;
        case 403:
            errorMessageText = 'Access denied. You do not have permission.';
            break;
        case 404:
            errorMessageText = 'Resource not found. Please try again later.';
            break;
        case 409:
            errorMessageText = 'Email already exists. Please use a different email.';
            break;
        case 500:
            errorMessageText = 'Server error. Please try again later.';
            break;
        default:
            errorMessageText = typeof error === 'string' ? error : 'An unexpected error occurred. Please try again.';
    }

    if (errorMessage) {
        errorMessage.textContent = errorMessageText;
        console.log(`Error (${statusCode}):`, error);
        errorMessage.style.display = 'block';
        setTimeout(() => {
            errorMessage.style.display = 'none';
        }, 3000);
    } else {
        console.error('Error message element not found');
    }
}