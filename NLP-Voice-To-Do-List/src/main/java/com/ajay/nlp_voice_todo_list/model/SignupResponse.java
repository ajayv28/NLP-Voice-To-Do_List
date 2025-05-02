package com.ajay.nlp_voice_todo_list.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse implements GenericResponse {

    Long id;
    String email;
    String password;

}
