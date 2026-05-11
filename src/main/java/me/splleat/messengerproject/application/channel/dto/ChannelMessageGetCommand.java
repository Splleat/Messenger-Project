package me.splleat.messengerproject.application.channel.dto;

public record ChannelMessageGetCommand(
        long userId,
        long channelId
) {}
