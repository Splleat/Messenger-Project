package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.domain.channel.ChannelType;

public record DirectChannelCreateCommand(
        long userId,
        long targetUserId,
        String channelName,
        ChannelType type
) {}
