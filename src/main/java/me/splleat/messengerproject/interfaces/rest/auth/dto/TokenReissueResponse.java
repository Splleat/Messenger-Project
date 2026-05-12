package me.splleat.messengerproject.interfaces.rest.auth.dto;

public record TokenReissueResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {}
