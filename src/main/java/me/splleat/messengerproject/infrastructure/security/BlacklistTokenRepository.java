package me.splleat.messengerproject.infrastructure.security;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.property.RedisKeyPrefixProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class BlacklistTokenRepository {
    private final RedisKeyPrefixProperties redisKeyPrefixProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String jti, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(redisKeyPrefixProperties.blacklist() + jti, "logout", Duration.ofMillis(ttlMillis));
    }

    public boolean existsByToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKeyPrefixProperties.blacklist() + token));
    }
}