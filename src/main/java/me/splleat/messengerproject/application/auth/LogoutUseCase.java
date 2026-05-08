package me.splleat.messengerproject.application.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.auth.dto.LogoutCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.infrastructure.security.BlacklistTokenRepository;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;

@UseCase
@RequiredArgsConstructor
public class LogoutUseCase {
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public void execute(LogoutCommand command) {
        try {
            removeRefreshToken(command.refreshToken());
        } catch (BusinessException _) {
            // 리프레시 토큰이 만료되었다면, 새로운 액세스 토큰을 발급받지 못하므로 무시
        }

        try {
            saveBlackList(command.accessToken());
        } catch (BusinessException _) {
            // 액세스 토큰이 만료되었다면, 블랙 리스트에 넣을 필요가 없으므로 무시
        }
    }

    private void saveBlackList(String accessToken) {
        Claims accessTokenClaims = jwtProvider.getClaims(accessToken);

        long expiration = jwtProvider.getExpiration(accessTokenClaims);

        long now = System.currentTimeMillis();

        String jti = jwtProvider.getJti(accessTokenClaims);
        long ttlMillis = expiration - now;

        if (ttlMillis < 0) {
            return;
        }

        blacklistTokenRepository.save(jti, ttlMillis);
    }

    private void removeRefreshToken(String refreshToken) {
        Claims refreshTokenClaims = jwtProvider.getClaims(refreshToken);

        String jti = jwtProvider.getJti(refreshTokenClaims);

        refreshTokenRepository.delete(jti);
    }
}
