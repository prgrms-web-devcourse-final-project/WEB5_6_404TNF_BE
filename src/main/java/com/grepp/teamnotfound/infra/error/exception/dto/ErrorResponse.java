package com.grepp.teamnotfound.infra.error.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String code, String message, LocalDateTime timestamp) {

}
