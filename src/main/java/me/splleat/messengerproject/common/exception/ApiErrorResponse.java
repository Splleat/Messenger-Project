package me.splleat.messengerproject.common.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
    public static ApiErrorResponse from(ErrorCode errorCode) {
        return new ApiErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }

    public static ApiErrorResponse of(ErrorCode errorCode, String message) {
        return new ApiErrorResponse(
                errorCode.getCode(),
                message,
                LocalDateTime.now()
        );
    }
}
