package me.splleat.messengerproject.domain.group.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class GroupMemberAlreadyExistsException extends BusinessException {
    public GroupMemberAlreadyExistsException() {
        super(ErrorCode.GROUP_MEMBER_ALREADY_EXISTS);
    }
}
