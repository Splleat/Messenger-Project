package me.splleat.messengerproject.application.space.dto;

import java.util.List;

public record SpaceInviteCommand(
        long userId,
        long spaceId,
        List<Long> targetIds
) {}
