package me.splleat.messengerproject.interfaces.rest.auth.request;

import me.splleat.messengerproject.application.auth.command.LogoutCommand;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
    public LogoutCommand toCommand() {
        return new LogoutCommand(accessToken, refreshToken);
    }
}
