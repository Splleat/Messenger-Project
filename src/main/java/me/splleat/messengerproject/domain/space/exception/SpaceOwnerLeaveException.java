package me.splleat.messengerproject.domain.space.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class SpaceOwnerLeaveException extends BusinessException {
    public SpaceOwnerLeaveException() {
        super(ErrorCode.SPACE_OWNER_CANNOT_LEAVE);
    }
}
