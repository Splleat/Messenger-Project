package me.splleat.messengerproject.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요한 서비스입니다."),
    TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다."),

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),

    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 일치하지 않습니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 일치하지 않습니다."),

    USER_DEACTIVATED(HttpStatus.FORBIDDEN, "비활성화된 사용자입니다."),
    TARGET_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다."),

    USER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 프로필이 존재하지 않습니다."),
    USER_PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "사용자 프로필이 이미 존재합니다."),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다."),
    SPACE_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널이 그룹에 속하지 않거나 존재하지 않습니다."),

    CHANNEL_USER_SETTING_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 채널의 사용자 설정이 존재합니다."),
    CHANNEL_USER_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "채널 참여 정보가 존재하지 않습니다."),

    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹이 존재하지 않습니다."),

    SPACE_MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 그룹에 포함된 멤버입니다."),
    SPACE_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹 멤버를 찾을 수 없습니다."),
    SPACE_MEMBER_NOT_PERMITTED(HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없는 그룹 멤버입니다."),
    SPACE_OWNER_CANNOT_LEAVE(HttpStatus.CONFLICT, "그룹장은 그룹을 탈퇴할 수 없습니다."),

    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),

    ATTACHMENT_UNSUPPORTED_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 첨부 파일 타입입니다"),
    ATTACHMENT_EXCEED_LIMIT_SIZE(HttpStatus.BAD_REQUEST, "첨부 파일 크기 제한을 초과했습니다."),
    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "첨부 파일을 찾을 수 없습니다."),

    LOCK_FAILED(HttpStatus.CONFLICT, "락을 획득하지 못했습니다."),

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
