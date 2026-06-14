package me.splleat.messengerproject.interfaces.rest.space.dto;

import jakarta.validation.constraints.NotEmpty;
import me.splleat.messengerproject.application.space.dto.SpaceInviteCommand;

import java.util.List;

public record SpaceInviteRequest(
        @NotEmpty
        List<Long> targetIds
) {
    public SpaceInviteCommand toCommand(long userId, long spaceId) {
        return new SpaceInviteCommand(userId, spaceId, targetIds);
    }
}
