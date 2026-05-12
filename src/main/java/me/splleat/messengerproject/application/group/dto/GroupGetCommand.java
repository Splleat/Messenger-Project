package me.splleat.messengerproject.application.group.dto;

public record GroupGetCommand(
    long userId,
    long groupId
) {
    public static GroupGetCommand of(long userId, long groupId) {
        return new GroupGetCommand(userId, groupId);
    }
}
