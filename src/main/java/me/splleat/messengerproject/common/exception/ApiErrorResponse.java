package me.splleat.messengerproject.common.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String message,
        LocalDateTime timestamp
) {
    public static ApiErrorResponse from(ErrorCode errorCode) {
        return new ApiErrorResponse(
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }

    public static ApiErrorResponse of(String message) {
        return new ApiErrorResponse(
                message,
                LocalDateTime.now()
        );
    }
}
