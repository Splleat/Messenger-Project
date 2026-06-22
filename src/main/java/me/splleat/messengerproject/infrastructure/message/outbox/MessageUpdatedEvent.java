package me.splleat.messengerproject.infrastructure.message.outbox;

import me.splleat.messengerproject.domain.message.Message;

public record MessageUpdatedEvent(
        long channelId,
        long messageId,
        String content
) {
    public static MessageUpdatedEvent from(Message message) {
        return new MessageUpdatedEvent(message.getChannelId(), message.getId(), message.getContent());
    }
}
