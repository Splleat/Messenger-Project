package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;

import java.time.LocalDateTime;
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
        LocalDateTime createdAt
) {
    public static MessageResponse of(String username, String profileUrl, Message message) {
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
            message.getCreatedAt()
        );
    }
}
