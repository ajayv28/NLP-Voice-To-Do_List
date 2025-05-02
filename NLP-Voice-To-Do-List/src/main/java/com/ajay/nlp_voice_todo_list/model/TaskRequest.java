package com.ajay.nlp_voice_todo_list.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "User ID cannot be empty")
    private Long userId;

    @NotBlank(message = "Task cannot be empty")
    private String task;

    @NotBlank(message = "Urgency cannot be empty")
    private String urgency;

    private String datetime;

}
