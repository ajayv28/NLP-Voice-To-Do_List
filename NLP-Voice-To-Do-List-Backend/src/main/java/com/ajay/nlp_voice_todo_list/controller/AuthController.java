package com.ajay.nlp_voice_todo_list.controller;

import com.ajay.nlp_voice_todo_list.config.JwtConfig;
import com.ajay.nlp_voice_todo_list.entity.User;
import com.ajay.nlp_voice_todo_list.model.ErrorResponse;
import com.ajay.nlp_voice_todo_list.model.GenericResponse;
import com.ajay.nlp_voice_todo_list.model.LoginResponse;
import com.ajay.nlp_voice_todo_list.model.SignupResponse;
import com.ajay.nlp_voice_todo_list.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<? extends GenericResponse> login(@RequestHeader("Authorization") String token) {

        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                if (!jwtConfig.isTokenExpired(token)) {
                    String email = jwtConfig.getUserEmail(token);
                    String password = jwtConfig.getUserPassword(token);

                    Optional<User> user = userRepository.findByEmail(email);
                    if(user.isPresent()){
                        if(user.get().getPassword().equals(password)){
                            LoginResponse response = LoginResponse.builder()
                                    .id(user.get().getId())
                                    .email(user.get().getEmail())
                                    .build();
                            return ResponseEntity.ok(response);
                        }
                    }
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                            body(new ErrorResponse("Invalid Credentials", 403));

                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                        body(new ErrorResponse("Token Expired", 403));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(new ErrorResponse("Invalid Token",403));


        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token Validation Failed" + e.getMessage(),403));
        }

    }


    @PostMapping("/signup")
    public ResponseEntity<? extends GenericResponse> signup(@RequestHeader("Authorization") String token) {

        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                if (!jwtConfig.isTokenExpired(token)) {
                    String email = jwtConfig.getUserEmail(token);
                    String password = jwtConfig.getUserPassword(token);

                    if (userRepository.existsByEmail(email)) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse("Email already exists", 403));
                    }

                    User user = new User();
                    user.setEmail(email);
                    user.setPassword(password);
                    User savedUser = userRepository.save(user);

                    SignupResponse response = SignupResponse.builder()
                            .id(savedUser.getId())
                            .email(savedUser.getEmail())
                            .password(savedUser.getPassword())
                            .build();
                    return ResponseEntity.ok(response);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                        body(new ErrorResponse("Token Expired", 403));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(new ErrorResponse("Invalid Token",403));

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token Validation Failed" + e.getMessage(),403));
        }
    }

}
