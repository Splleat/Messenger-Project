package me.splleat.messengerproject.interfaces.rest.auth.dto;

import me.splleat.messengerproject.application.auth.dto.LogoutCommand;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
    public LogoutCommand toCommand() {
        return new LogoutCommand(accessToken, refreshToken);
    }
}
