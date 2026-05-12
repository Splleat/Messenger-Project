package me.splleat.messengerproject.interfaces.rest.auth.dto;

import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long id,
    String username,
    String profileImage,
    String statusMessage,
    long accessTokenExpiresIn
) {
    public static LoginResponse of(User user, UserProfile userProfile, TokenResult accessToken, TokenResult refreshToken) {
        return new LoginResponse(
                accessToken.token(),
                refreshToken.token(),
                user.getId(),
                userProfile.getName(),
                userProfile.getImageUrl(),
                userProfile.getStatusMessage(),
                accessToken.expirationMillis()
        );
    }
}
