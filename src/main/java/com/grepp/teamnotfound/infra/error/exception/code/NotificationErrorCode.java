package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode{

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NOTI_001", "존재하지 않는 알림입니다."),
    NOTIFICATION_EXPIRED(HttpStatus.BAD_REQUEST.value(), "NOTI_002", "이미 확인한 알림입니다.");

    private final int status;
    private final String code;
    private final String message;
}
