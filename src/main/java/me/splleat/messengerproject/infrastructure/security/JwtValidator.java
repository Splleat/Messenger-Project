package me.splleat.messengerproject.infrastructure.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtValidator {
    private final JwtProvider jwtProvider;
    private final BlacklistTokenRepository blacklistTokenRepository;

    public Authentication validateAndGetAuthentication(String token) {
        Claims claims = jwtProvider.getClaims(token);
        String jti = jwtProvider.getJti(claims);

        if (blacklistTokenRepository.existsByToken(jti)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        long userId = jwtProvider.getUserId(claims);
        boolean isAdmin = jwtProvider.getIsAdmin(claims);

        UserPrincipal principal = UserPrincipal.create(userId, isAdmin);

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
