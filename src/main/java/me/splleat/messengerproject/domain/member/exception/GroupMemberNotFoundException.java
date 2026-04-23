package me.splleat.messengerproject.domain.member.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class GroupMemberNotFoundException extends BusinessException {
    public GroupMemberNotFoundException() {
        super(ErrorCode.GROUP_MEMBER_NOT_FOUND);
    }
}
