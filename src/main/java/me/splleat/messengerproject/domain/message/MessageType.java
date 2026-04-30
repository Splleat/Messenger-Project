package me.splleat.messengerproject.domain.message;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

public enum MessageType {
    DIRECT,
    GROUP;

    public static MessageType from(String value) {
        try {
            return MessageType.valueOf(value);
        } catch (IllegalArgumentException _) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
