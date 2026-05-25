package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

import java.util.List;

public record MessageSlice(
        List<MessageResponse> messages,
        boolean hasMore
) {
    public Long prevCursorId() {
        return messages.isEmpty() ? null : messages.getFirst().id();
    }

    public Long nextCursorId() {
        return messages.isEmpty() ? null : messages.getLast().id();
    }
}
