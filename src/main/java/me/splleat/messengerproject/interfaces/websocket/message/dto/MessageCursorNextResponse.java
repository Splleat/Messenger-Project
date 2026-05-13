package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record MessageCursorNextResponse(
        List<MessageResponse> messages,
        boolean hasNext,
        Long nextCursorId
) {
    public static MessageCursorNextResponse from(Slice<MessageResponse> next) {
        List<MessageResponse> nextContent = next.getContent();

        Long nextCursorId = nextContent.isEmpty() ? null : nextContent.getLast().id();

        return new MessageCursorNextResponse(
                nextContent,
                next.hasNext(),
                nextCursorId
        );
    }
}
