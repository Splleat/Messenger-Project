package me.splleat.messengerproject.infrastructure.message.event;

public record NotificationEvent(
        long recipientUserId,
        Long spaceId,
        long channelId,
        long messageId,
        String senderName,
        String content
) {}
