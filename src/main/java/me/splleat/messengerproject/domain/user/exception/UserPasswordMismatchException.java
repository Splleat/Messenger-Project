package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class UserPasswordMismatchException extends BusinessException {
    public UserPasswordMismatchException() {
        super(ErrorCode.USER_PASSWORD_MISMATCH);
    }
}
