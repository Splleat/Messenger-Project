package me.splleat.messengerproject.application.group.dto;

import java.util.List;

public record GroupInviteCommand(
        long userId,
        long groupId,
        List<Long> targetIds
) {}
