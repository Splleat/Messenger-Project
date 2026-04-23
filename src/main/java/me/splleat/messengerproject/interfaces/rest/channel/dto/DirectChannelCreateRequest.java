package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.application.channel.dto.DirectChannelCreateCommand;
import me.splleat.messengerproject.domain.channel.ChannelType;

public record DirectChannelCreateRequest(
        String channelName,
        ChannelType type
) {
    public DirectChannelCreateCommand toCommand(long userId) {
        return new DirectChannelCreateCommand(userId, channelName, type);
    }
}
