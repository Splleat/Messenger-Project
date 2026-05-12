package me.splleat.messengerproject.application.channel.dto;

public record ChannelParticipantGetCommand(
        long userId,
        long channelId
) {
    public static ChannelParticipantGetCommand of(long userId, long channelId) {
        return new ChannelParticipantGetCommand(userId, channelId);
    }
}
