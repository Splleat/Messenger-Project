package me.splleat.messengerproject.application.auth.command;

public record LogoutCommand(
        String accessToken,
        String refreshToken
) {}
