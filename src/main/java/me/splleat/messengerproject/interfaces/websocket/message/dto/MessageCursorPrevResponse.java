package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record MessageCursorPrevResponse(
        List<MessageResponse> messages,
        boolean hasPrev,
        Long prevCursorId
) {
    public static MessageCursorPrevResponse from(Slice<MessageResponse> prev) {
        List<MessageResponse> prevContent = prev.getContent();

        Long prevCursorId = prevContent.isEmpty() ? null : prevContent.getFirst().id();

        return new MessageCursorPrevResponse(
                prevContent,
                prev.hasNext(),
                prevCursorId
        );
    }
}
