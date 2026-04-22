package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class UserEmailDuplicatedException extends BusinessException {
    public UserEmailDuplicatedException() {
        super(ErrorCode.EMAIL_DUPLICATED);
    }
}
