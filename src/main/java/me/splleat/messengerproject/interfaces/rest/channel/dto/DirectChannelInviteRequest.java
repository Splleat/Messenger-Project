package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.application.channel.dto.DirectChannelInviteCommand;

import java.util.List;

public record DirectChannelInviteRequest(
        List<Long> targetIds
) {
    public DirectChannelInviteCommand toCommand(long userId, long channelId) {
        return new DirectChannelInviteCommand(userId, channelId, targetIds);
    }
}
