package me.splleat.messengerproject.interfaces.rest.profile.dto;

import me.splleat.messengerproject.application.profile.dto.UserProfileUpdateCommand;

public record UserProfileUpdateRequest(
        String name,
        String statusMessage
) {
    public UserProfileUpdateCommand toCommand(long userId) {
        return new UserProfileUpdateCommand(userId, name, statusMessage);
    }
}
