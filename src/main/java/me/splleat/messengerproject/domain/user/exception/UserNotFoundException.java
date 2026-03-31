package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.global.exception.BusinessException;
import me.splleat.messengerproject.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
