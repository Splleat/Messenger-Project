package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record MessageCursorNextResponse(
        List<MessageResponse> messages,
        boolean hasNext,
        long nextCursorId
) {
    public static MessageCursorNextResponse from(Slice<MessageResponse> next) {
        return new MessageCursorNextResponse(
                next.getContent(),
                next.hasNext(),
                next.getContent().getLast().id()
        );
    }
}
