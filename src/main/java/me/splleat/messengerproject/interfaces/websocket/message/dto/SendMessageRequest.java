package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.application.message.dto.SendMessageCommand;
import me.splleat.messengerproject.domain.message.MessageType;

import java.util.UUID;

public record SendMessageRequest(
        String username,
        String profileUrl,
        String content,
        String idempotencyKey,
        String type,
        Long parentMessageId
) {
    public SendMessageCommand toCommand(long senderId, long channelId) {
        return new SendMessageCommand(
                senderId,
                channelId,
                content,
                UUID.fromString(idempotencyKey),
                MessageType.from(type),
                parentMessageId
        );
    }
}
