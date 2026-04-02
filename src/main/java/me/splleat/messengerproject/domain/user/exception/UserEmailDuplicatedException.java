package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.global.exception.BusinessException;
import me.splleat.messengerproject.global.exception.ErrorCode;

public class UserEmailDuplicatedException extends BusinessException {
    public UserEmailDuplicatedException() {
        super(ErrorCode.EMAIL_DUPLICATED);
    }
}
