package me.splleat.messengerproject.interfaces.websocket.message.dto;

public record MessageCursorCommand(
        long userId,
        long channelId,
        long cursorId
) {
    public static MessageCursorCommand of(long userId, long channelId, long cursorId) {
        return new MessageCursorCommand(userId, channelId, cursorId);
    }
}
