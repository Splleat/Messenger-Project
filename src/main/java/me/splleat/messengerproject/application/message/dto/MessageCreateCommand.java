package me.splleat.messengerproject.application.message.dto;

import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;

import java.util.UUID;

public record MessageCreateCommand(
        long senderId,
        long channelId,
        String content,
        UUID idempotencyKey,
        MessageType type,
        Long parentMessageId
) {
    public Message toEntity() {
        return Message.create(senderId, channelId, content, type, parentMessageId, idempotencyKey);
    }
}
