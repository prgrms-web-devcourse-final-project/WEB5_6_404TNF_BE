package com.grepp.teamnotfound.app.controller.api.auth.payload;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerificationRequest {

    @Email
    private String email;
}
