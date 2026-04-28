package me.splleat.messengerproject.application.message.dto;

import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;
import me.splleat.messengerproject.domain.user.User;

import java.util.UUID;

public record MessageCreateCommand(
        long senderId,
        long channelId,
        String content,
        UUID idempotencyKey,
        MessageType type,
        Long parentMessageId
) {
    public Message toEntity(User user, Channel channel) {
        // MVP -> 첨부파일 제외
        return Message.create(user, channel, content, type, parentMessageId, idempotencyKey, null);
    }
}
