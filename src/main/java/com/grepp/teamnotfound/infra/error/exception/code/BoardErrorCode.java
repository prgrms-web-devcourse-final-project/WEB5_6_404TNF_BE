package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {

    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "BOARD_001", "존재하지 않는 게시글입니다."),
    BOARD_DELETED(HttpStatus.NOT_FOUND.value(), "BOARD_002", "삭제된 게시글입니다."),
    BOARD_REPORTED(HttpStatus.NOT_FOUND.value(), "BOARD_003", "관리자가 숨김 처리한 게시글입니다."),
    // 좋아요 정책 확인 필요
    BOARD_ALREADY_LIKED(HttpStatus.BAD_REQUEST.value(), "BOARD_004", "이미 좋아요를 눌렀습니다."),
    BOARD_NOT_LIKED_YET(HttpStatus.NOT_FOUND.value(), "BOARD_005", "좋아요를 누른 상태가 아닙니다."),

    BOARD_INVALID_SORT(HttpStatus.BAD_REQUEST.value(), "BOARD_006", "유효하지 않은 정렬 방식입니다."),
    BOARD_INVALID_SEARCH(HttpStatus.BAD_REQUEST.value(), "BOARD_007", "유효하지 않은 검색 방식입니다."),
    BOARD_INVALID_PAGE(HttpStatus.BAD_REQUEST.value(), "BOARD_008", "유효하지 않은 페이지입니다."),

    BOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "BOARD_009", "해당 게시글에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}
