package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.application.message.dto.MessageResult;
import me.splleat.messengerproject.domain.message.MessageType;

import java.time.LocalDateTime;

public record MessageResponse(
        long userId,
        long channelId,
        String username,
        String profileUrl,
        String content,
        MessageType type,
        Long parentMessageId,
        LocalDateTime createdAt
) {
    public static MessageResponse from(MessageResult result) {
        return new MessageResponse(
            result.userId(),
            result.channelId(),
            result.username(),
            result.profileUrl(),
            result.content(),
            result.type(),
            result.parentMessageId(),
            result.createdAt()
        );
    }
}
