package me.splleat.messengerproject.domain.channel.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class SpaceChannelNotFoundException extends BusinessException {
    public SpaceChannelNotFoundException() {
        super(ErrorCode.SPACE_CHANNEL_NOT_FOUND);
    }
}
