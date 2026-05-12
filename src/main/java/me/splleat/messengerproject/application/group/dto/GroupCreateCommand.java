package me.splleat.messengerproject.application.group.dto;

public record GroupCreateCommand(
        Long userId,
        String groupName
) {}
