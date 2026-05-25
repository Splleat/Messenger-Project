package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageSlice;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

import java.util.List;

public record ChannelMessagePageResponse(
        List<MessageResponse> messages,
        boolean hasMore,
        Long cursorId
) {
    public static ChannelMessagePageResponse prev(MessageSlice slice) {
        return new ChannelMessagePageResponse(slice.messages(), slice.hasMore(), slice.prevCursorId());
    }

    public static ChannelMessagePageResponse next(MessageSlice slice) {
        return new ChannelMessagePageResponse(slice.messages(), slice.hasMore(), slice.nextCursorId());
    }
}
