package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.domain.channel.Channel;

public record ChannelListResult(
        long channelId,
        String channelName
) {
    public static ChannelListResult from(Channel channel) {
        return new ChannelListResult(
                channel.getId(),
                channel.getName()
        );
    }
}
