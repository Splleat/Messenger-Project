package me.splleat.messengerproject.application.channel.dto;

public record GroupChannelEnterCommand(
        long userId,
        long channelId,
        long groupId
) {
    public static GroupChannelEnterCommand of(long userId, long channelId, long groupId) {
        return new GroupChannelEnterCommand(userId, channelId, groupId);
    }
}