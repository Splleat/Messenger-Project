package me.splleat.messengerproject.application.space.dto;

public record SpaceCreateCommand(
        Long userId,
        String spaceName
) {}
