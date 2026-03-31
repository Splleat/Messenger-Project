package me.splleat.messengerproject.application.command;

import me.splleat.messengerproject.interfaces.rest.request.LoginRequest;

public record LoginCommand(
        String email,
        String password
) {
    public static LoginCommand from(LoginRequest request) {
        return new LoginCommand(
                request.email(),
                request.password()
        );
    }
}
