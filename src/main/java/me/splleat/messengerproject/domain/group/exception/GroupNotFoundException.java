package me.splleat.messengerproject.domain.group.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class GroupNotFoundException extends BusinessException {
    public GroupNotFoundException() {
        super(ErrorCode.GROUP_NOT_FOUND);
    }
}
