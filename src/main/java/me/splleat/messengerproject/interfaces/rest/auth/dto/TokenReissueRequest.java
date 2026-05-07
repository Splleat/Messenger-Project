package me.splleat.messengerproject.interfaces.rest.auth.dto;

import me.splleat.messengerproject.application.auth.dto.TokenReissueCommand;

public record TokenReissueRequest(
        String accessToken,
        String refreshToken
) {
    public TokenReissueCommand toCommand() {
        return new TokenReissueCommand(accessToken, refreshToken);
    }
}
