package me.splleat.messengerproject.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.splleat.messengerproject.domain.user.UserRole;
import me.splleat.messengerproject.global.exception.BusinessException;
import me.splleat.messengerproject.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public String createAccessToken(long userId, UserRole role) {
        Date accessExpiration = new Date();

        accessExpiration.setTime(accessExpiration.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .expiration(accessExpiration)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(long userId) {
        Date refreshExpiration = new Date();

        refreshExpiration.setTime(refreshExpiration.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(refreshExpiration)
                .signWith(secretKey)
                .compact();
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

    public String getUserRole(Claims claims) {
        return claims.get("role").toString();
    }
}
