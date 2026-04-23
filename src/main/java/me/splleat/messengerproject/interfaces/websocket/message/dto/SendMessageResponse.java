package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.application.message.dto.SendMessageResult;
import me.splleat.messengerproject.domain.message.MessageType;

import java.time.LocalDateTime;

public record SendMessageResponse(
        long userId,
        long channelId,
        String username,
        String profileUrl,
        String content,
        MessageType type,
        Long parentMessageId,
        LocalDateTime createdAt
) {
    public static SendMessageResponse from(SendMessageResult result) {
        return new SendMessageResponse(
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
