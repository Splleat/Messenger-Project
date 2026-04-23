package me.splleat.messengerproject.domain.member.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class GroupMemberNotPermittedException extends BusinessException {
    public GroupMemberNotPermittedException() {
        super(ErrorCode.GROUP_MEMBER_NOT_PERMITTED);
    }
}
