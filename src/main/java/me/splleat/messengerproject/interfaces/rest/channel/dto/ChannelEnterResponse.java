package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageSlice;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

import java.util.List;
import java.util.stream.Stream;

public record ChannelEnterResponse(
        List<MessageResponse> messages,
        boolean hasPrev,
        boolean hasNext,
        Long prevCursorId,
        Long nextCursorId,
        Long lastReadMessageId
) {
    public static ChannelEnterResponse around(MessageSlice prev, MessageSlice next, Long lastReadMessageId) {
        List<MessageResponse> prevContent = prev.messages();
        List<MessageResponse> nextContent = next.messages();

        Long prevCursorId = prevContent.isEmpty() ? null : prevContent.getFirst().id();
        Long nextCursorId = nextContent.isEmpty() ? null : nextContent.getLast().id();

        return new ChannelEnterResponse(
                Stream.concat(prevContent.stream(), nextContent.stream()).toList(),
                prev.hasMore(),
                next.hasMore(),
                prevCursorId,
                nextCursorId,
                lastReadMessageId
        );
    }

    public static ChannelEnterResponse newest(MessageSlice newest) {
        List<MessageResponse> newestContent = newest.messages();

        return new ChannelEnterResponse(
                newestContent,
                newest.hasMore(),
                false,
                newest.prevCursorId(),
                null,
                null
        );
    }
}
