package me.splleat.messengerproject.application.message.dto;

import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;
import me.splleat.messengerproject.interfaces.websocket.message.dto.AttachmentCreateRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateCommand(
        long senderId,
        long channelId,
        String content,
        UUID idempotencyKey,
        MessageType type,
        Long parentMessageId,
        List<AttachmentCreateRequest> attachments
) {
    public Message toMessage() {
        return Message.create(senderId, channelId, content, type, parentMessageId, idempotencyKey);
    }
}
