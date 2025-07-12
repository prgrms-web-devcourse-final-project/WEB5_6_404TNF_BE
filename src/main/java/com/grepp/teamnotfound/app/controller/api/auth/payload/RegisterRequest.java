package com.grepp.teamnotfound.app.controller.api.auth.payload;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    private String email;
    private String name;
    private String nickname;
    private String password;
}
