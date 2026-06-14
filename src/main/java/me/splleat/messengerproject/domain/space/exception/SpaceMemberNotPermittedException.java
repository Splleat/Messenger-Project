package me.splleat.messengerproject.domain.space.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class SpaceMemberNotPermittedException extends BusinessException {
    public SpaceMemberNotPermittedException() {
        super(ErrorCode.SPACE_MEMBER_NOT_PERMITTED);
    }
}
