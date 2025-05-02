package com.ajay.nlp_voice_todo_list.model;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse implements GenericResponse {

    private String message;

    private Integer statusCode;

}
