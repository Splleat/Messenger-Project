package me.splleat.messengerproject.infrastructure.security;

import io.jsonwebtoken.Claims;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtValidatorTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private BlacklistTokenRepository blacklistTokenRepository;

    @InjectMocks
    private JwtValidator jwtValidator;

    @Mock
    private Claims claims;

    @Test
    @DisplayName("블랙리스트에 등록된 토큰인 경우 예외가 발생한다.")
    void validateAndGetAuthentication_WhenTokenInBlacklist_ThrowsException() {
        // given
        String token = "blacklisted.token";
        String jti = "jti-123";

        given(jwtProvider.getClaims(token))
                .willReturn(claims);
        given(jwtProvider.getJti(claims))
                .willReturn(jti);
        given(blacklistTokenRepository.existsByToken(jti))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtValidator.validateAndGetAuthentication(token))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_INVALID);
    }

    @Test
    @DisplayName("유효한 토큰인 경우 인증 객체를 반환한다.")
    void validateAndGetAuthentication_WhenValidToken_ReturnsAuthentication() {
        // given
        String token = "valid.token";
        String jti = "jti-456";
        long userId = 1L;
        boolean isAdmin = false;

        given(jwtProvider.getClaims(token))
                .willReturn(claims);
        given(jwtProvider.getJti(claims))
                .willReturn(jti);
        given(blacklistTokenRepository.existsByToken(jti))
                .willReturn(false);
        given(jwtProvider.getUserId(claims))
                .willReturn(userId);
        given(jwtProvider.getIsAdmin(claims))
                .willReturn(isAdmin);

        // when
        Authentication authentication = jwtValidator.validateAndGetAuthentication(token);

        // then
        assertThat(authentication)
                .isNotNull();
        assertThat(authentication.getPrincipal())
                .isInstanceOf(UserPrincipal.class);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        assertThat(principal.getUserId())
                .isEqualTo(userId);
        assertThat(principal.isAdmin())
                .isEqualTo(isAdmin);
    }
}
