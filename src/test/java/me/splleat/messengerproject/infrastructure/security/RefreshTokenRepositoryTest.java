package me.splleat.messengerproject.infrastructure.security;

import me.splleat.messengerproject.support.TestContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@Import({
        TestContainerConfig.class,
        RefreshTokenRepository.class
})
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("저장한 리프레시 토큰은 userId로 조회할 수 있다.")
    void save_WhenRefreshToken_CanFindByUserId() {
        // given
        long userId = 1L;
        String token = "token";

        refreshTokenRepository.save(userId, token);

        // when
        Optional<String> found = refreshTokenRepository.findById(userId);

        // then
        assertThat(found)
                .isPresent()
                .hasValue(token);
    }
}