package com.grepp.teamnotfound.infra.error.exception;

import com.grepp.teamnotfound.infra.error.exception.code.BaseErrorCode;

public class BusinessException extends CommonException {

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
