package me.splleat.messengerproject.application.auth.dto;

public record LogoutCommand(
        String accessToken,
        String refreshToken
) {}
