package me.splleat.messengerproject.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private static final String KEY_PREFIX = "refresh:";
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String jti, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + jti, refreshToken, Duration.ofMillis(expirationMillis));
    }

    public Optional<String> findByJti(String jti) {
        return Optional.ofNullable(redisTemplate
                .opsForValue().get(KEY_PREFIX + jti));
    }

    public void delete(String jti) {
        redisTemplate.delete(KEY_PREFIX + jti);
    }
}
