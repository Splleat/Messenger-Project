package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        long id,
        long userId,
        long channelId,
        String username,
        String profileUrl,
        String content,
        UUID idemPotencyKey,
        MessageType type,
        Long parentMessageId,
        List<AttachmentResponse> attachments,
        LocalDateTime createdAt
) {
    public MessageResponse(
            long id,
            long userId,
            long channelId,
            String username,
            String profileUrl,
            String content,
            UUID idemPotencyKey,
            MessageType type,
            Long parentMessageId,
            LocalDateTime createdAt
    ) {
        this(id, userId, channelId, username, profileUrl, content, idemPotencyKey, type, parentMessageId, List.of(), createdAt);
    }

    public static MessageResponse of(String username, String profileUrl, Message message, List<AttachmentResponse> attachments) {
        return new MessageResponse(
            message.getId(),
            message.getUserId(),
            message.getChannelId(),
            username,
            profileUrl,
            message.getContent(),
            message.getIdemPotencyKey(),
            message.getType(),
            message.getParentMessageId(),
            attachments,
            message.getCreatedAt()
        );
    }
}
