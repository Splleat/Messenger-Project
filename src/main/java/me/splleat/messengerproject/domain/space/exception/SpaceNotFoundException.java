package me.splleat.messengerproject.domain.space.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class SpaceNotFoundException extends BusinessException {
    public SpaceNotFoundException() {
        super(ErrorCode.SPACE_NOT_FOUND);
    }
}
