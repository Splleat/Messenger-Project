package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Stream;

public record MessageCursorBothResponse(
        List<MessageResponse> messages,
        boolean hasPrev,
        boolean hasNext,
        Long prevCursorId,
        Long nextCursorId
) {
    public static MessageCursorBothResponse of(Slice<MessageResponse> prev, Slice<MessageResponse> next) {
        List<MessageResponse> prevContent = prev.getContent();
        List<MessageResponse> nextContent = next.getContent();

        Long prevCursorId = prevContent.isEmpty() ? null : prevContent.getFirst().id();
        Long nextCursorId = nextContent.isEmpty() ? null : nextContent.getLast().id();

        return new MessageCursorBothResponse(
                Stream.concat(prevContent.stream(), nextContent.stream()).toList(),
                prev.hasNext(),
                next.hasNext(),
                prevCursorId,
                nextCursorId
        );
    }

    public static MessageCursorBothResponse newest(Slice<MessageResponse> prev) {
        List<MessageResponse> prevContent = prev.getContent();

        Long prevCursorId = prevContent.isEmpty() ? null : prevContent.getFirst().id();
        Long nextCursorId = prevContent.isEmpty() ? null : prevContent.getLast().id();

        return new MessageCursorBothResponse(
                prevContent,
                prev.hasNext(),
                false,
                prevCursorId,
                nextCursorId
        );
    }
}
