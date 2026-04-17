package me.splleat.messengerproject.interfaces.rest.request;

import me.splleat.messengerproject.application.command.LogoutCommand;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
    public LogoutCommand toCommand() {
        return new LogoutCommand(accessToken, refreshToken);
    }
}
