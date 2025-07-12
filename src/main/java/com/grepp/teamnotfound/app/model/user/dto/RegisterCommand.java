package com.grepp.teamnotfound.app.model.user.dto;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
public class RegisterCommand {

    private final String email;
    private final String name;
    private final String nickname;
    private final String password;
}
