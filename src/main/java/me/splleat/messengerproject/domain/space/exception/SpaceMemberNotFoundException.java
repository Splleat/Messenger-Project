package me.splleat.messengerproject.domain.space.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class SpaceMemberNotFoundException extends BusinessException {
    public SpaceMemberNotFoundException() {
        super(ErrorCode.SPACE_MEMBER_NOT_FOUND);
    }
}
