package me.splleat.messengerproject.application.auth.dto;

public record TokenReissueCommand(
        String accessToken,
        String refreshToken
) {}
