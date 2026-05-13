package me.splleat.messengerproject.application.channel.dto;

public record DirectChannelEnterCommand(
        long userId,
        long channelId
) {
    public static DirectChannelEnterCommand of(long userId, long channelId) {
        return new DirectChannelEnterCommand(userId, channelId);
    }
}
