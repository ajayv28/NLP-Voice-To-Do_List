package com.ajay.nlp_voice_todo_list.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserData {

    private String id;
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String locale;
    private boolean verifiedEmail;

}
