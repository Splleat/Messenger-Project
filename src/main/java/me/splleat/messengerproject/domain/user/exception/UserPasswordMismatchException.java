package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.global.exception.BusinessException;
import me.splleat.messengerproject.global.exception.ErrorCode;

public class UserPasswordMismatchException extends BusinessException {
    public UserPasswordMismatchException() {
        super(ErrorCode.USER_PASSWORD_MISMATCH);
    }
}
