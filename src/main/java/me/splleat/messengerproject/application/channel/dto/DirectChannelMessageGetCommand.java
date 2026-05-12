package me.splleat.messengerproject.application.channel.dto;

public record DirectChannelMessageGetCommand(
        long userId,
        long channelId
) {}
