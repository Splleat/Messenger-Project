package me.splleat.messengerproject.domain.channel.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class ChannelUserSettingAlreadyExistsException extends BusinessException {
    public ChannelUserSettingAlreadyExistsException() {
        super(ErrorCode.CHANNEL_USER_SETTING_ALREADY_EXISTS);
    }
}
