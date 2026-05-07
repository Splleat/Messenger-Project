package me.splleat.messengerproject.interfaces.rest.auth.dto;

import me.splleat.messengerproject.application.auth.dto.TokenReissueResult;

public record TokenReissueResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {
    public static TokenReissueResponse from(TokenReissueResult result) {
        return new TokenReissueResponse(result.accessToken(), result.refreshToken(), result.accessTokenExpiresIn());
    }
}
