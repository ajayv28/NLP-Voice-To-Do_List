package com.ajay.nlp_voice_todo_list.controller;

import com.ajay.nlp_voice_todo_list.entity.User;
import com.ajay.nlp_voice_todo_list.model.ErrorResponse;
import com.ajay.nlp_voice_todo_list.model.GenericResponse;
import com.ajay.nlp_voice_todo_list.model.LoginResponse;
import com.ajay.nlp_voice_todo_list.model.SocialLoginRequest;
import com.ajay.nlp_voice_todo_list.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/social-auth")
public class SocialAuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<? extends GenericResponse> socialLogin(@RequestBody SocialLoginRequest request) {
        try {
            String email = request.getSocialUserData().getEmail();

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setPassword("social_" + request.getSocialUserData().getId());
                        return userRepository.save(newUser);
                    });

            LoginResponse response = LoginResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to process social login", 500));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<? extends GenericResponse> socialSignup(@RequestBody SocialLoginRequest request) {
        try {
            String email = request.getSocialUserData().getEmail();

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("User already exists with this email",409));
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword("social_" + request.getSocialUserData().getId());
            User savedUser = userRepository.save(newUser);

            LoginResponse response = LoginResponse.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to process social login", 500));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

}
