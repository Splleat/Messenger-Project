package me.splleat.messengerproject.domain.group.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class GroupOwnerLeaveException extends BusinessException {
    public GroupOwnerLeaveException() {
        super(ErrorCode.GROUP_OWNER_CANNOT_LEAVE);
    }
}
