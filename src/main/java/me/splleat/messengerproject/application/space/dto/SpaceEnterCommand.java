package me.splleat.messengerproject.application.space.dto;

public record SpaceEnterCommand(
        long userId,
        long spaceId,
        String nickname
) {}
