package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

import java.util.Arrays;

public enum CursorDirection {
    PREV,
    NEXT;

    public static CursorDirection from(String direction) {
        return Arrays.stream(CursorDirection.values())
                .filter(dir -> direction.equalsIgnoreCase(dir.name()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT));
    }
}
