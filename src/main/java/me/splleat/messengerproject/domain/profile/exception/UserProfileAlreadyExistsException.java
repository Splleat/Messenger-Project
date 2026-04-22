package me.splleat.messengerproject.domain.profile.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class UserProfileAlreadyExistsException extends BusinessException {
    public UserProfileAlreadyExistsException() {
        super(ErrorCode.USER_PROFILE_ALREADY_EXISTS);
    }
}
