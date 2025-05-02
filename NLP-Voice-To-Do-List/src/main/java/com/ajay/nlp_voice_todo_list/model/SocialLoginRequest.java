package com.ajay.nlp_voice_todo_list.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginRequest {

    private String provider;

    private String code;

    private SocialUserData socialUserData;

}
