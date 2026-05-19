package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Stream;

public record MessagePageResponse(
        List<MessageResponse> messages,
        boolean hasPrev,
        boolean hasNext,
        Long prevCursorId,
        Long nextCursorId
) {
    public static MessagePageResponse of(Slice<MessageResponse> prev, Slice<MessageResponse> next) {
        List<MessageResponse> prevContent = prev.getContent();
        List<MessageResponse> nextContent = next.getContent();

        Long prevCursorId = prevContent.isEmpty() ? null : prevContent.getFirst().id();
        Long nextCursorId = nextContent.isEmpty() ? null : nextContent.getLast().id();

        return new MessagePageResponse(
                Stream.concat(prevContent.stream(), nextContent.stream()).toList(),
                prev.hasNext(),
                next.hasNext(),
                prevCursorId,
                nextCursorId
        );
    }

    public static MessagePageResponse newest(Slice<MessageResponse> prev) {
        List<MessageResponse> prevContent = prev.getContent();

        Long prevCursorId = prevContent.isEmpty() ? null : prevContent.getFirst().id();
        Long nextCursorId = prevContent.isEmpty() ? null : prevContent.getLast().id();

        return new MessagePageResponse(
                prevContent,
                prev.hasNext(),
                false,
                prevCursorId,
                nextCursorId
        );
    }
}
