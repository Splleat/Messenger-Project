package me.splleat.messengerproject.interfaces.rest.response;

import me.splleat.messengerproject.application.auth.result.LoginResult;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    String username,
    String profileImage,
    String statusMessage
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.username(),
                result.profileImage(),
                result.statusMessage()
        );
    }
}
