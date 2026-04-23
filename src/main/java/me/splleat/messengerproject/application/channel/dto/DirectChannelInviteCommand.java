package me.splleat.messengerproject.application.channel.dto;

import java.util.List;

public record DirectChannelInviteCommand(
        long userId,
        long channelId,
        List<Long> targetIds
) {}
