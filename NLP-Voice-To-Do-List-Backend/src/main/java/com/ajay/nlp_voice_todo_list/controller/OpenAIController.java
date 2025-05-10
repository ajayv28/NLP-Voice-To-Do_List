package com.ajay.nlp_voice_todo_list.controller;

import com.ajay.nlp_voice_todo_list.common.StringConstants;
import com.ajay.nlp_voice_todo_list.entity.Task;
import com.ajay.nlp_voice_todo_list.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    @GetMapping
    public ResponseEntity<?> getOpenAiToken() {

        try{
            return ResponseEntity.ok(StringConstants.openAiToken);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
