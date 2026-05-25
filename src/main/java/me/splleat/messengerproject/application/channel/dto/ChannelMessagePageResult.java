package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageSlice;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;

import java.util.List;

public record ChannelMessagePageResult(
        List<MessageResponse> messages,
        boolean hasMore,
        Long cursorId
) {
    public static ChannelMessagePageResult prev(MessageSlice slice) {
        return new ChannelMessagePageResult(slice.messages(), slice.hasMore(), slice.prevCursorId());
    }

    public static ChannelMessagePageResult next(MessageSlice slice) {
        return new ChannelMessagePageResult(slice.messages(), slice.hasMore(), slice.nextCursorId());
    }
}
