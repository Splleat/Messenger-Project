package me.splleat.messengerproject.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "토큰이 만료되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_004", "인증이 필요한 서비스입니다."),

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "AUTH_005", "이미 사용 중인 이메일입니다."),

    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디나 비밀번호가 일치하지 않습니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디나 비밀번호가 일치하지 않습니다."),

    USER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_PROFILE_001", "사용자 프로필이 존재하지 않습니다."),
    USER_PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_PROFILE_002", "사용자 프로필이 이미 존재합니다."),

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
