package me.splleat.messengerproject.infrastructure.security;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.global.constant.RedisTtl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private static final String KEY_PREFIX = "refresh:";
    private final RedisTemplate<String, String> redisTemplate;

    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + userId, refreshToken, RedisTtl.REFRESH_TOKEN);
    }

    public Optional<String> findById(Long userId) {
        return Optional.ofNullable(redisTemplate
                .opsForValue().get(KEY_PREFIX + userId));
    }

    public void deleteById(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
