package com.ajay.nlp_voice_todo_list.controller;

import com.ajay.nlp_voice_todo_list.model.ErrorResponse;
import com.ajay.nlp_voice_todo_list.entity.Task;
import com.ajay.nlp_voice_todo_list.model.TaskRequest;
import com.ajay.nlp_voice_todo_list.entity.User;
import com.ajay.nlp_voice_todo_list.repository.UserRepository;
import com.ajay.nlp_voice_todo_list.service.NotificationService;
import com.ajay.nlp_voice_todo_list.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {                //TODO : USER NOT LINKED

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;


    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest taskRequest){

        if(taskRequest.getUserId() != null && !userRepository.existsById(taskRequest.getUserId())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(new ErrorResponse("Invalid User ID stored in Local Storage", 404));
        }

        try {
            Task createdTask = taskService.createTask(taskRequest.getTask(), taskRequest.getUrgency(), taskRequest.getDatetime(), taskRequest.getUserId());

            if (createdTask != null && taskRequest.getUserId() != null) {
                Optional<User> user = userRepository.findById(taskRequest.getUserId());
                user.ifPresent(userData -> notificationService.sendTaskNotifications(taskRequest, userData, false));
            }
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(new ErrorResponse("Error creating task " + e.getMessage(), 500));
        }
    }


    @GetMapping
    public ResponseEntity<List<Task>> getTask(@RequestParam Long userId) {

        List<Task> tasks = taskService.getTask(userId);

        return ResponseEntity.ok(tasks);

    }


    @PutMapping
    public ResponseEntity<Task> updateTask(@RequestParam Long taskId, @RequestBody TaskRequest taskRequest) {

        Task updatedTask = taskService.updateTask(taskId, taskRequest.getTask(), taskRequest.getUrgency(), taskRequest.getDatetime().toString());

        if(updatedTask != null) {
            if (taskRequest.getUserId() != null) {
                Optional<User> user = userRepository.findById(taskRequest.getUserId());
                user.ifPresent(userData -> notificationService.sendTaskNotifications(taskRequest, userData, true));
            }
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@RequestParam Long taskId) {

        boolean deleted = taskService.deleteTask(taskId);

        if(deleted)
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.notFound().build();
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)       // To handle any TaskRequest in non-desired format
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {

        StringBuilder errorMessage = new StringBuilder("Task Creation failed: ");

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        return new ResponseEntity<>(ErrorResponse.builder()
                .message(errorMessage.toString())
                .build(), HttpStatus.BAD_REQUEST);
    }
}
