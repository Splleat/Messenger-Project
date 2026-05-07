package me.splleat.messengerproject.application.channel.dto;

public record DirectChannelLeaveCommand(
        long userId,
        long channelId
) {}
