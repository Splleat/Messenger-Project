package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class UserDeactivatedException extends BusinessException {
    public UserDeactivatedException() {
        super(ErrorCode.USER_DEACTIVATED);
    }
}
