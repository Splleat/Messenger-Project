package me.splleat.messengerproject.interfaces.websocket.typing.dto;

import me.splleat.messengerproject.application.typing.dto.TypingCommand;

public record TypingRequest(
        boolean isTyping
) {
    public TypingCommand toCommand(long userId, long channelId) {
        return new TypingCommand(userId, channelId, isTyping);
    }
}
