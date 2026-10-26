package me.splleat.messengerproject.infrastructure.security;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.property.RedisKeyPrefixProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final RedisKeyPrefixProperties redisKeyPrefixProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String jti, String refreshToken, long expirationMillis) {
        long ttl = expirationMillis - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue()
                    .set(redisKeyPrefixProperties.refresh() + jti, refreshToken, Duration.ofMillis(ttl));
        }
    }

    public Optional<String> findByJti(String jti) {
        return Optional.ofNullable(redisTemplate
                .opsForValue().get(redisKeyPrefixProperties.refresh() + jti));
    }

    public void delete(String jti) {
        redisTemplate.delete(redisKeyPrefixProperties.refresh() + jti);
    }

    public boolean existsByJti(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKeyPrefixProperties.refresh() + jti));
    }
}
