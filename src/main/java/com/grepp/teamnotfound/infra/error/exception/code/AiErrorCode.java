package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements BaseErrorCode {

    AI_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "AI_001", "AI 호출에 실패했습니다."),
    AI_ANALYSIS_DATA_FAILED(HttpStatus.NOT_FOUND.value(), "AI_002", "충분한 관찰노트가 작성되지 않아, AI 감정분석에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
