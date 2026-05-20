package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.application.channel.CursorDirection;

public record ChannelMessageCursorCommand(
        long userId,
        long channelId,
        long cursorId,
        CursorDirection direction
) {
    public static ChannelMessageCursorCommand of(long userId, long channelId, long cursorId, String direction) {
        return new ChannelMessageCursorCommand(userId, channelId, cursorId, CursorDirection.from(direction));
    }
}
