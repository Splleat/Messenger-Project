package me.splleat.messengerproject.application.space.dto;

import me.splleat.messengerproject.domain.space.Space;

public record SpaceListResult(
        long spaceId,
        String spaceName
) {
    public static SpaceListResult from(Space space) {
        return new SpaceListResult(space.getId(), space.getName());
    }
}
