package me.splleat.messengerproject.interfaces.rest.auth.dto;

public record TokenReissueResult(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {}
