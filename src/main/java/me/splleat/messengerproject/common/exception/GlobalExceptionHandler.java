package me.splleat.messengerproject.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiErrorResponse errorResponse = ApiErrorResponse.from(errorCode);

        if (errorCode.getStatus().is5xxServerError()) {
            log.error("BusinessException(서버 오류): {}", e.getMessage(), e);
        } else {
            // 4xx는 클라이언트 오류 -> 스택 트레이스 출력 X
            log.debug("BusinessException(클라이언트 오류) [{}]: {}", errorCode.name(), e.getMessage());
        }

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.INVALID_INPUT.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.of(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleServerException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiErrorResponse errorResponse = ApiErrorResponse.from(errorCode);

        log.error("서버 오류 발생: {}", e.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }
}
