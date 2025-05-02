import {main as processWithAi} from "./openAi-integration.js"       //for NLP with openAI
import { backendURL } from "./config.js";

(function checkAuth() {                                              // Immediately Invoked Function Expression - to load on reaching this endpoint
    const userData = localStorage.getItem('userData');
    if (!userData) {
        window.location.replace('login.html');
        return;
    }
})();

let confirmationSection = document.getElementById('task-detail-id');

document.addEventListener('DOMContentLoaded', () => {
    confirmationSection.style.display = 'none';
    const voiceButton = document.querySelector('.voice-btn');
    if (voiceButton){
        voiceButton.addEventListener('click',startListening);
    }
    clearTaskOutput();
    updateTaskList();

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('userData');
            window.location.href = 'login.html';
        });
    }
});


function getUserData() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

function getUrgencyColor(urgency) {
    if (urgency == null)
        return
    switch (urgency.toLowerCase()) {
        case 'high':
            return '#FF0000';
        case 'medium':
            return '#FFA500';
        case 'low':
            return '#008000';
        default:
            return '#808080';
    }
}


function clearTaskOutput (){
    const taskInfo = document.querySelector('.task-info');  
    if(taskInfo){
        document.getElementById('task').textContent='';
        document.getElementById('urgency').textContent='';
        document.getElementById('datetime').textContent='';            
    }
    const confirmationArea =  document.getElementById('confirmation-area');
    if(confirmationArea){
        confirmationArea.innerHTML = '';
    }
}


function startListening(){
    if ('webkitSpeechRecognition' in window) {
        const recognition = new webkitSpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-US';

        recognition.onstart = function(){
            clearTaskOutput();
        };
        recognition.onresult = function(event){
            const transcript = event.results[0][0].transcript;
            processVoiceCommand(transcript);
        };
        recognition.onerror = function(event){
            console.error("Speech recognition error:", event.error);
        };
            
        recognition.start();
           
    } else {
        alert("Speech recognition not supported in this browser.");
    }
}

async function processVoiceCommand(command) {
    try{
        const aiResp = await processWithAi(command);
        const aiResponse = JSON.parse(aiResp);  

        const userData = getUserData();
        if (!userData) {
            throw new Error('User is not authenticated');
        }

        if(aiResponse != null){

            const requestBody = {
                task : aiResponse.task,
                urgency : aiResponse.urgency,
                datetime : aiResponse.datetime,
                userId: userData.id
            }

            document.getElementById('task').textContent = aiResponse.task;
            document.getElementById('urgency').textContent = aiResponse.urgency;
            document.getElementById('datetime').textContent = aiResponse.datetime;
  
            const confirmationArea = document.getElementById('confirmation-area');
            confirmationSection.style.display = 'block';
            confirmationArea.innerHTML = `
                <div class='confirmation-button'>
                    <p>Is it correct</p>
                    <button onclick='window.confirmTask(true)' class='confirm-btn'> YES </button>
                    <button onclick='window.confirmTask(false)' class='confirm-btn'> NO </button>
                </div>
            `;
    

            window.confirmTask = async (isCorrect) => {  //declaring as global function, so that <button.... can access it, as we created it dynamically using temp literal
        
                if(isCorrect){
                    try {
                        confirmationSection.style.display = 'none';
                        confirmationArea.innerHTML = '';

                        const response = await fetch(`${backendURL}/api/tasks`, {           
                            method: 'POST',
                            headers: {
                                'Content-type': 'application/json',
                                'Accept': 'application/json',
                            },
                            body: JSON.stringify(requestBody)
                            });
                            
                        if(!response.ok){
                            console.log("API Request to create task unsuccessful");
                            throw new Error(`Http error with status code ${response.status}`);
                        }

                        const responseData = await response.json();
                        updateTaskList();
                        return responseData;

                    } catch (error) {
                        console.error("Error creating new task with API call to backend", error);
                        alert("Failed to add task. Please try again.");
                    }
            
                } else {
                        console.log("User said the displayed info is wrong. Pls retry");
                        confirmationSection.style.display = 'none';
                        confirmationArea.innerHTML = '';
                        alert("Since the intepretted content is wrong, kindly re record the task");
                }
            }
        }
    } catch (error) {
        console.log("Error processing voice command", error);
        alert("Error in intepretting content, kindly re record the task or contact support");
        return null;
    }
}


async function getTasksFromDb(userId) {
    try {
        const userData = getUserData();
        if (!userData) {
            throw new Error('User not authenticated');
        }

        const response = await fetch(`${backendURL}/api/tasks?userId=${userData.id}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error during GET call to backend ! status: ${response.status}`);
        }

        const data = await response.json();
        return Array.isArray(data) ? data : [];
    } catch (error) {
        console.error("Error fetching tasks:", error);
        return []; 
    }
}


function updateTaskList() {
    const todoList = document.getElementById("todo-list");
    todoList.innerHTML = '';
    todoList.innerHTML = `
        <h2>My Followups : </h2>
    `;
    
    const userData = getUserData();

    getTasksFromDb(userData.id).then(tasks => {
        tasks.forEach((taskData, index) => {
            const listItem = document.createElement("div");
            listItem.classList.add('todo-item');

            const statusIndicator = document.createElement("div");
            statusIndicator.classList.add('status-indicator');
            statusIndicator.style.backgroundColor = getUrgencyColor(taskData.urgency);

            const taskContent = document.createElement("div");
            taskContent.classList.add('task-content');

            const taskTitle = document.createElement("div");
            taskTitle.classList.add('task-title');
            taskTitle.innerHTML = `
                <span class="task-name">${taskData.task}</span>
            `;

            const taskDetails = document.createElement("div");
            taskDetails.classList.add('task-details-line');
            taskDetails.innerHTML = `
                <span class="urgency-badge" style="background-color: ${getUrgencyColor(taskData.urgency)}">${taskData.urgency}</span>
                ${taskData.datetime ? `<span class="datetime">${taskData.datetime}</span>` : ''}
            `;

            taskContent.appendChild(taskTitle);
            taskContent.appendChild(taskDetails);

            const completeButton = document.createElement("button");
            completeButton.classList.add('complete-btn');
            completeButton.innerHTML = '';
            completeButton.innerText = 'Mark Done';
            completeButton.title = 'Mark as Completed';
            completeButton.onclick = async () => {
                try {
                    const response = await fetch(`${backendURL}/api/tasks?taskId=${taskData.id}`, {
                        method: 'DELETE',
                        headers: {
                            "Content-Type": "application/json",
                            "Accept": "application/json"
                        }
                    });

                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }

                    updateTaskList();
                } catch (error) {
                    console.error("Error deleting task:", error);
                    alert("Failed to delete task. Please try again.");
                }
            };

            listItem.appendChild(statusIndicator);
            listItem.appendChild(taskContent);
            listItem.appendChild(completeButton);

            todoList.appendChild(listItem);
        });
    }).catch(error => {
        console.error("Error updating task list:", error);
    });
}
