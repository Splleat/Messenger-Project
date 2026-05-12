package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.domain.channel.Channel;

public record ChannelListResponse(
    long channelId,
    String channelName
) {
    public static ChannelListResponse from(Channel channel) {
        return new ChannelListResponse(channel.getId(), channel.getName());
    }
}
