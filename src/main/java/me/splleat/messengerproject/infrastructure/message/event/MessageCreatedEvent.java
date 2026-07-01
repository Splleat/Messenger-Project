package me.splleat.messengerproject.infrastructure.message.event;

import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

public record MessageCreatedEvent(
        MessageResponse message
) {
    public static MessageCreatedEvent from(MessageResponse message) {
        return new MessageCreatedEvent(message);
    }
}
