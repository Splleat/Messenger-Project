package me.splleat.messengerproject.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage, null, false, false);
        this.errorCode = errorCode;
    }
}