package me.splleat.messengerproject.domain.user.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class TargetUserNotFoundException extends BusinessException {
    public TargetUserNotFoundException() {
        super(ErrorCode.TARGET_USER_NOT_FOUND);
    }
}
