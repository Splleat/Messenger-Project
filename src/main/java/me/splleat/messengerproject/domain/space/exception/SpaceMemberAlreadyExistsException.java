package me.splleat.messengerproject.domain.space.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class SpaceMemberAlreadyExistsException extends BusinessException {
    public SpaceMemberAlreadyExistsException() {
        super(ErrorCode.SPACE_MEMBER_ALREADY_EXISTS);
    }
}
