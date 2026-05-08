package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.application.channel.dto.ChannelListResult;

public record ChannelListResponse(
    long channelId,
    String channelName
) {
    public static ChannelListResponse from(ChannelListResult result) {
        return new ChannelListResponse(result.channelId(), result.channelName());
    }
}
