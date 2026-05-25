package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageSlice;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

import java.util.List;
import java.util.stream.Stream;

public record ChannelEnterResult(
        List<MessageResponse> messages,
        boolean hasPrev,
        boolean hasNext,
        Long prevCursorId,
        Long nextCursorId,
        Long lastReadMessageId
) {
    public static ChannelEnterResult around(MessageSlice prev, MessageSlice next, Long lastReadMessageId) {
        List<MessageResponse> prevContent = prev.messages();
        List<MessageResponse> nextContent = next.messages();

        return new ChannelEnterResult(
                Stream.concat(prevContent.stream(), nextContent.stream()).toList(),
                prev.hasMore(),
                next.hasMore(),
                prev.prevCursorId(),
                next.nextCursorId(),
                lastReadMessageId
        );
    }

    public static ChannelEnterResult newest(MessageSlice newest) {
        List<MessageResponse> newestContent = newest.messages();

        return new ChannelEnterResult(
                newestContent,
                newest.hasMore(),
                false,
                newest.prevCursorId(),
                newest.nextCursorId(),
                null
        );
    }
}
