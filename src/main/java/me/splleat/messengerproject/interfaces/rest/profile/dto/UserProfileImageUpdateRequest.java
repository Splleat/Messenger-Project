package me.splleat.messengerproject.interfaces.rest.profile.dto;

import me.splleat.messengerproject.application.profile.dto.UserProfileImageUpdateCommand;

public record UserProfileImageUpdateRequest(
        String newImageKey
) {
    public UserProfileImageUpdateCommand toCommand(long userId) {
        return new UserProfileImageUpdateCommand(userId, newImageKey);
    }
}
