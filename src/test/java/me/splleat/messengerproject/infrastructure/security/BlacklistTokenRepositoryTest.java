package me.splleat.messengerproject.infrastructure.security;

import me.splleat.messengerproject.support.TestContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@Import({
        TestContainerConfig.class,
        BlacklistTokenRepository.class
})
class BlacklistTokenRepositoryTest {

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    @Test
    @DisplayName("저장한 블랙리스트는 jti로 존재 여부를 확인할 수 있다.")
    void save_WhenBlacklistToken_CanFindsByJti() {
        // given
        String jti = "jti";
        long ttlMillis = 60 * 60 * 60;

        blacklistTokenRepository.save(jti, ttlMillis);

        // when
        boolean found = blacklistTokenRepository.existsByToken(jti);

        assertThat(found)
                .isTrue();
    }
}