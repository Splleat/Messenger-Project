package me.splleat.messengerproject.infrastructure.message.event;

public record TypingEvent(
        long userId,
        long channelId,
        String username,
        boolean isTyping
) {}
