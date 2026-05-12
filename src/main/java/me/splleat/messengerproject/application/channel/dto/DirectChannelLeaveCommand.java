package me.splleat.messengerproject.application.channel.dto;

public record DirectChannelLeaveCommand(
        long userId,
        long channelId
) {
    public static DirectChannelLeaveCommand of(long userId, long channelId) {
        return new DirectChannelLeaveCommand(userId, channelId);
    }
}
