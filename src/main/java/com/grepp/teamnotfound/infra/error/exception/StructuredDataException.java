package com.grepp.teamnotfound.infra.error.exception;

import com.grepp.teamnotfound.infra.error.exception.code.BaseErrorCode;

public class StructuredDataException extends CommonException {

    public StructuredDataException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
