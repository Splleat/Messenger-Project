package me.splleat.messengerproject.infrastructure.security;

import io.jsonwebtoken.Claims;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.common.property.JwtProperties;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    static class TestFixture {
        static String secretKey = "this-is-a-test-secret-key-with-at-least-32-characters-long";
        static JwtProvider jwtProvider(long expiration) {
            JwtProperties jwtProperties = new JwtProperties(secretKey, expiration, expiration);

            return new JwtProvider(jwtProperties);
        }
    }

    @Test
    @DisplayName("액세스 토큰을 생성하면 jti, 토큰, 만료 시간을 포함한 결과를 반환한다.")
    void createAccessToken_ReturnsTokenResult() {
        // given
        Long userId = 1L;
        boolean isAdmin = true;
        JwtProvider jwtProvider = TestFixture.jwtProvider(1800000);

        // when
        TokenResult result = jwtProvider.createAccessToken(userId, isAdmin);

        // then
        assertThat(result.jti())
                .isNotNull();
        assertThat(result.token())
                .isNotNull();
        assertThat(result.expirationMillis())
                .isGreaterThan(System.currentTimeMillis());

        Claims claims = jwtProvider.getClaims(result.token());

        assertThat(jwtProvider.getUserId(claims))
                .isEqualTo(userId);
        assertThat(jwtProvider.getIsAdmin(claims))
                .isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰을 생성하면 jti, 토큰, 만료 시간을 포함한 결과를 반환한다.")
    void createRefreshToken_ReturnsTokenResult() {
        // given
        Long userId = 1L;
        JwtProvider jwtProvider = TestFixture.jwtProvider(1800000);

        // when
        TokenResult result = jwtProvider.createRefreshToken(userId);

        // then
        assertThat(result.jti())
                .isNotNull();
        assertThat(result.token())
                .isNotNull();
        assertThat(result.expirationMillis())
                .isGreaterThan(System.currentTimeMillis());

        Claims claims = jwtProvider.getClaims(result.token());

        assertThat(jwtProvider.getUserId(claims))
                .isEqualTo(userId);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 클레임을 조회하면 예외가 발생한다.")
    void getClaims_WithInvalidToken_ThrowsException() {
        // given
        String invalidToken = "invalid.token.here";
        JwtProvider jwtProvider = TestFixture.jwtProvider(1800000);

        // when & then
        assertThatThrownBy(() -> jwtProvider.getClaims(invalidToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_INVALID);
    }

    @Test
    @DisplayName("만료된 토큰의 클레임을 조회하면 TOKEN_EXPIRED 예외가 발생한다.")
    void getClaims_WithExpiredToken_ThrowsException() {
        // given
        JwtProvider jwtProvider = TestFixture.jwtProvider(-1000);
        TokenResult result = jwtProvider.createAccessToken(1L, false);
        String expiredToken = result.token();

        // when & then
        assertThatThrownBy(() -> jwtProvider.getClaims(expiredToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("만료된 토큰은 getClaimsIgnoreExpiration로 클레임을 획득할 수 있다.")
    void getClaimsIgnoreExpiration_WithExpiredToken_ReturnsClaims() {
        // given
        long userId = 1L;
        JwtProvider jwtProvider = TestFixture.jwtProvider(-1000);
        TokenResult result = jwtProvider.createAccessToken(userId, false);

        // when
        Claims claims = jwtProvider.getClaimsIgnoreExpiration(result.token());

        // then
        assertThat(claims)
                .isNotNull();
        assertThat(jwtProvider.getUserId(claims))
                .isEqualTo(userId);
    }
}
