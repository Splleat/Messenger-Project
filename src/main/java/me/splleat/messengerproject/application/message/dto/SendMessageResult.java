package me.splleat.messengerproject.application.message.dto;

import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;

import java.time.LocalDateTime;

public record SendMessageResult(
        long userId,
        long channelId,
        String username,
        String profileUrl,
        String content,
        MessageType type,
        Long parentMessageId,
        LocalDateTime createdAt
) {
    public static SendMessageResult from(Message message, String username, String profileUrl) {
        return new SendMessageResult(
            message.getUserId(),
            message.getChannelId(),
            username,
            profileUrl,
            message.getContent(),
            message.getType(),
            message.getParentMessageId(),
            message.getCreatedAt()
        );
    }
}
