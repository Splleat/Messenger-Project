package me.splleat.messengerproject.interfaces.rest.auth.response;

import me.splleat.messengerproject.application.auth.result.LoginResult;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record LoginResponse(
    String accessToken,
    String refreshToken,

    @JsonSerialize(using = ToStringSerializer.class)
    Long id,

    String username,
    String profileImage,
    String statusMessage
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.id(),
                result.username(),
                result.profileImage(),
                result.statusMessage()
        );
    }
}
