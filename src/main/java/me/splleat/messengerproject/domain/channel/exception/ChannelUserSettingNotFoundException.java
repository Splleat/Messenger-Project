package me.splleat.messengerproject.domain.channel.exception;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public class ChannelUserSettingNotFoundException extends BusinessException {
    public ChannelUserSettingNotFoundException() {
        super(ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND);
    }
}
