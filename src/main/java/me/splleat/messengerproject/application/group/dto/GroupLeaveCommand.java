package me.splleat.messengerproject.application.group.dto;

public record GroupLeaveCommand(
        long userId,
        long groupId
) {}
