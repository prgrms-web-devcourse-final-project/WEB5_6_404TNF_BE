package com.grepp.teamnotfound.app.controller.api.auth.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyRequest {

    private String email;
    private String verificationCode;

}
