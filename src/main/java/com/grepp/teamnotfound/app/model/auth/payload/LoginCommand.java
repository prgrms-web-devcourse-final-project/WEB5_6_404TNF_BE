package com.grepp.teamnotfound.app.model.auth.payload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommand {

    private String email;
    private String password;

}
