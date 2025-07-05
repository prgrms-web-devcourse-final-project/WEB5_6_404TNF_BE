package com.grepp.teamnotfound.infra.error.exception.code;

public interface BaseErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
