package me.splleat.messengerproject.interfaces.rest.auth.dto;

import jakarta.validation.constraints.NotBlank;
import me.splleat.messengerproject.application.auth.dto.TokenReissueCommand;

public record TokenReissueRequest(
        @NotBlank(message = "액세스 토큰은 필수 입력값입니다.")
        String accessToken,

        @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
        String refreshToken
) {
    public TokenReissueCommand toCommand() {
        return new TokenReissueCommand(accessToken, refreshToken);
    }
}
