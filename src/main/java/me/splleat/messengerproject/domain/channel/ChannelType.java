package me.splleat.messengerproject.domain.channel;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public enum ChannelType {
    TEXT;

    public static ChannelType from(String value) {
        try {
            return ChannelType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
