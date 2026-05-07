package me.splleat.messengerproject.application.auth.dto;

public record TokenReissueResult(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {}
