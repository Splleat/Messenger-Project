package me.splleat.messengerproject.application.channel.dto;

public record ChannelListResult(
    long channelId,
    String channelName,
    boolean hasUnread
) {}
