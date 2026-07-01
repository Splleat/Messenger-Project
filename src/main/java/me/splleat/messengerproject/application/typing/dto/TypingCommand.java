package me.splleat.messengerproject.application.typing.dto;

import me.splleat.messengerproject.infrastructure.message.event.TypingEvent;

public record TypingCommand(
        long userId,
        long channelId,
        boolean isTyping
) {
    public TypingEvent toEvent(String username) {
        return new TypingEvent(userId, channelId, username, isTyping);
    }
}
