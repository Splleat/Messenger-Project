package me.splleat.messengerproject.infrastructure.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

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
        String userRole = jwtProvider.getUserRole(claims);

        return new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(userRole)));
    }
}
