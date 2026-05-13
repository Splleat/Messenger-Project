package me.splleat.messengerproject.interfaces.websocket.message.dto;

import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Stream;

public record MessageCursorBothResponse(
        List<MessageResponse> messages,
        boolean hasPrev,
        boolean hasNext,
        long prevCursorId,
        long nextCursorId
) {
    public static MessageCursorBothResponse of(Slice<MessageResponse> prev, Slice<MessageResponse> next) {
        return new MessageCursorBothResponse(
                Stream.concat(prev.getContent().stream(), next.getContent().stream()).toList(),
                prev.hasNext(),
                next.hasNext(),
                prev.getContent().getFirst().id(),
                next.getContent().getLast().id()
        );
    }

    public static MessageCursorBothResponse newest(Slice<MessageResponse> prev) {
        return new MessageCursorBothResponse(
                prev.getContent(),
                prev.hasNext(),
                false,
                prev.getContent().getFirst().id(),
                prev.getContent().getLast().id()
        );
    }
}
