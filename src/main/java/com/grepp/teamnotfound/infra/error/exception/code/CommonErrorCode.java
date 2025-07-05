package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "COMMON_001", "요청하신 리소스를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "COMMON_002", "잘못된 요청입니다."),
    INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR.value(), "COMMON_003", "서버 내부 오류입니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS.value(), "COMMON_004", "너무 많은 요청입니다."),
    FILE_TOO_BIG(HttpStatus.INTERNAL_SERVER_ERROR.value(), "FILE_001", "파일 용량이 초과되었습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "FILE_002", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "FILE_003", "파일 삭제에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

}
