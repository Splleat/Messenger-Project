package me.splleat.messengerproject.application.group.dto;

public record GroupLeaveCommand(
        long userId,
        long groupId
) {
    public static GroupLeaveCommand of(long userId, long groupId) {
        return new GroupLeaveCommand(userId, groupId);
    }
}
