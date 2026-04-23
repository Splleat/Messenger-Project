package me.splleat.messengerproject.application.channel.dto;

import me.splleat.messengerproject.domain.channel.ChannelType;

public record GroupChannelCreateCommand(
        long userId,
        long groupId,
        String channelName,
        ChannelType type
) {}