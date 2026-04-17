package me.splleat.messengerproject.application.command;

public record LogoutCommand(
        String accessToken,
        String refreshToken
) {}
