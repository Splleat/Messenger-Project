package me.splleat.messengerproject.interfaces.rest.auth.dto;

import me.splleat.messengerproject.application.auth.dto.LoginResult;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long id,
    String username,
    String profileImage,
    String statusMessage,
    long accessTokenExpiresIn
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.id(),
                result.username(),
                result.profileImage(),
                result.statusMessage(),
                result.accessTokenExpiresIn()
        );
    }
}
