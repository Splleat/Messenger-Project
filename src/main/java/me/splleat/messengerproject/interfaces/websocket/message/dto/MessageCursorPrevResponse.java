package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record MessageCursorPrevResponse(
        List<MessageResponse> messages,
        boolean hasPrev,
        long prevCursorId
) {
    public static MessageCursorPrevResponse from(Slice<MessageResponse> prev) {
        return new MessageCursorPrevResponse(
                prev.getContent(),
                prev.hasNext(),
                prev.getContent().getFirst().id()
        );
    }
}
