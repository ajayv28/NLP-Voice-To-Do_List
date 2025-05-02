package com.ajay.nlp_voice_todo_list.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse implements GenericResponse {

    Long id;
    String email;

}
