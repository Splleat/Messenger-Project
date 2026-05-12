package me.splleat.messengerproject.application.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.application.auth.dto.TokenReissueCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;
import me.splleat.messengerproject.interfaces.rest.auth.dto.TokenReissueResponse;

import java.util.Objects;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class TokenReissueUseCase {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenReissueResponse execute(TokenReissueCommand command) {
        String oldAccessToken = command.accessToken();
        String oldRefreshToken = command.refreshToken();

        Claims rtClaims = jwtProvider.getClaims(oldRefreshToken);
        String rtJti = jwtProvider.getJti(rtClaims);

        // 저장된 리프레시 토큰 확인
        if (!refreshTokenRepository.existsByJti(rtJti)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        long userId = jwtProvider.getUserId(rtClaims);

        // 사용자 Role 갱신을 위해 DB에서 조회
        User user = userService.getActiveUser(userId);

        // 만료된 액세스 토큰을 파싱해 저장된 리프레시 토큰과 비교
        Claims atClaims = jwtProvider.getClaimsIgnoreExpiration(oldAccessToken);

        if (!Objects.equals(atClaims.getSubject(), rtClaims.getSubject())) {
            throw new BusinessException(ErrorCode.TOKEN_MISMATCH);
        }

        // 새로운 액세스 토큰과 리프레시 토큰 발급
        TokenResult newAccessToken = jwtProvider.createAccessToken(userId, user.isAdmin());
        TokenResult newRefreshToken = jwtProvider.createRefreshToken(userId);

        // 새로운 리프레시 토큰 저장
        refreshTokenRepository.save(newRefreshToken.jti(), newRefreshToken.token(), newRefreshToken.expirationMillis());

        // 기존에 저장된 리프레시 토큰 삭제
        refreshTokenRepository.delete(rtJti);

        return new TokenReissueResponse(newAccessToken.token(), newRefreshToken.token(), newAccessToken.expirationMillis());
    }
}
