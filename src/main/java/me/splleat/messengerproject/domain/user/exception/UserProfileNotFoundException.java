package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class UserProfileNotFoundException extends BusinessException {
    public UserProfileNotFoundException() {
        super(ErrorCode.USER_PROFILE_NOT_FOUND);
    }
}