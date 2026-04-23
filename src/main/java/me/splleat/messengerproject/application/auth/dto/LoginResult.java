package me.splleat.messengerproject.application.auth.dto;

import me.splleat.messengerproject.domain.profile.UserProfile;

public record LoginResult(
        String accessToken,
        String refreshToken,
        Long id,
        String username,
        String profileImage,
        String statusMessage
) {
    public static LoginResult of(String accessToken, String refreshToken, Long id, UserProfile profile) {
        return new LoginResult(
                accessToken,
                refreshToken,
                id,
                profile.getName(),
                profile.getImageUrl(),
                profile.getStatusMessage()
        );
    }
}
