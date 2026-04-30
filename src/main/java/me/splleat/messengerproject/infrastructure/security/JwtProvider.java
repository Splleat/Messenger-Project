package me.splleat.messengerproject.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKeyString,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public TokenResult createAccessToken(Long userId, boolean isAdmin) {
        String jti = UUID.randomUUID().toString();
        Date accessExpiration = new Date();

        accessExpiration.setTime(accessExpiration.getTime() + accessTokenExpiration);

        String accessToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .id(jti)
                .claim("isAdmin", isAdmin)
                .expiration(accessExpiration)
                .signWith(secretKey)
                .compact();

        return new TokenResult(jti, accessToken, accessExpiration.getTime());
    }

    public TokenResult createRefreshToken(Long userId) {
        String jti = UUID.randomUUID().toString();
        Date refreshExpiration = new Date();

        refreshExpiration.setTime(refreshExpiration.getTime() + refreshTokenExpiration);

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .id(jti)
                .expiration(refreshExpiration)
                .signWith(secretKey)
                .compact();

        return new TokenResult(jti, refreshToken, refreshExpiration.getTime());
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException _) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException _) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }

    public long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public boolean getIsAdmin(Claims claims) {
        return claims.get("isAdmin",Boolean.class);
    }

    public String getJti(Claims claims) {
        return claims.getId();
    }

    public long getExpiration(Claims claims) {
        return claims.getExpiration().getTime();
    }
}
