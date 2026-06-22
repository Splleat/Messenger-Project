package me.splleat.messengerproject.infrastructure.message.outbox;

import me.splleat.messengerproject.domain.message.Message;

public record MessageDeletedEvent(
        long channelId,
        long messageId
) {
    public static MessageDeletedEvent from(Message message) {
        return new MessageDeletedEvent(message.getChannelId(), message.getId());
    }
}
