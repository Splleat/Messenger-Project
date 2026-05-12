package me.splleat.messengerproject.application.channel.dto;

public record GroupChannelMessageGetCommand(
        long userId,
        long groupId,
        long channelId
) {
    public static GroupChannelMessageGetCommand of(long userId, long groupId, long channelId) {
        return new GroupChannelMessageGetCommand(userId, groupId, channelId);
    }
}
