package me.splleat.messengerproject.application.group.dto;

public record GroupEnterCommand(
        long userId,
        long groupId,
        String nickname
) {}
