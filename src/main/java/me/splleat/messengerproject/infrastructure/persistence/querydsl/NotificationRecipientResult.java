package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import me.splleat.messengerproject.infrastructure.message.event.NotificationEvent;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

public record NotificationRecipientResult(
        long recipientUserId,
        boolean isMuted
) {
    public NotificationEvent toEvent(Long spaceId, MessageResponse message) {
        return new NotificationEvent(
                recipientUserId,
                spaceId,
                message.channelId(),
                message.id(),
                message.username(),
                message.content()
        );
    }
}
