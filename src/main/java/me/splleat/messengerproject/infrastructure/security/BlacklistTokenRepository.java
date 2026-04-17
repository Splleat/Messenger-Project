package me.splleat.messengerproject.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class BlacklistTokenRepository {
    private static final String KEY_PREFIX = "blacklist:";
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String jti, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + jti, "logout", Duration.ofMillis(ttlMillis));
    }

    public boolean existsByToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + token));
    }
}
