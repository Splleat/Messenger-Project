package me.splleat.messengerproject.domain.channel.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class GroupChannelNotFoundException extends BusinessException {
    public GroupChannelNotFoundException() {
        super(ErrorCode.GROUP_CHANNEL_NOT_FOUND);
    }
}
