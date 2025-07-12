package com.grepp.teamnotfound.app.controller.api.auth.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
