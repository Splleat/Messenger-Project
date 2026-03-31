package me.splleat.messengerproject.application.auth.result;

import me.splleat.messengerproject.domain.profile.UserProfile;

public record LoginResult(
        String accessToken,
        String refreshToken,
        String username,
        String profileImage,
        String statusMessage
) {
    public static LoginResult of(String accessToken, String refreshToken, UserProfile profile) {
        return new LoginResult(
                accessToken,
                refreshToken,
                profile.getName(),
                profile.getImageUrl(),
                profile.getStatusMessage()
        );
    }
}
