package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.application.channel.dto.GroupChannelCreateCommand;
import me.splleat.messengerproject.domain.channel.ChannelType;

public record GroupChannelCreateRequest(
        String channelName,
        ChannelType type
) {
    public GroupChannelCreateCommand toCommand(long userId, long groupId) {
        return new GroupChannelCreateCommand(userId, groupId, channelName, type);
    }
}
