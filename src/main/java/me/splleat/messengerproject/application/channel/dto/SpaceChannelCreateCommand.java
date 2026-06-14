package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.domain.channel.ChannelType;

public record SpaceChannelCreateCommand(
        long userId,
        long spaceId,
        String channelName,
        ChannelType type
) {}