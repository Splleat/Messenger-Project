package me.splleat.messengerproject.application.profile.dto;

import me.splleat.messengerproject.domain.user.UserProfile;

public record UserProfileResult(
        long userId,
        String name,
        String statusMessage,
        String imageUrl
) {
    public static UserProfileResult from(UserProfile profile, String absoluteUrl) {
        return new UserProfileResult(profile.getUserId(), profile.getName(), profile.getStatusMessage(), absoluteUrl);
    }
}
