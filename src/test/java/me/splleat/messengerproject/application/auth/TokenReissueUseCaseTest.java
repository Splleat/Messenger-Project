package me.splleat.messengerproject.application.auth;

import io.jsonwebtoken.Claims;
import me.splleat.messengerproject.application.auth.dto.TokenReissueCommand;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;
import me.splleat.messengerproject.interfaces.rest.auth.dto.TokenReissueResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class TokenReissueUseCaseTest {
    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenReissueUseCase tokenReissueUseCase;

    @Test
    @DisplayName("토큰이 재발급되면 새로운 액세스 토큰과 리프레시 토큰을 반환한다.")
    void execute_WhenTokenReissued_ReturnsNewTokens() {
        // given
        long userId = 1L;
        Claims atClaims = mock(Claims.class);
        Claims rtClaims = mock(Claims.class);
        String rtJti = "jti";
        User user = mock(User.class);
        TokenReissueCommand command = new TokenReissueCommand("accessToken", "refreshToken");

        TokenResult newAccessToken = new TokenResult("newAtJti", "newAccessToken", 1800000L);
        TokenResult newRefreshToken = new TokenResult("newRtJti", "newRefreshToken", 604800000L);

        given(jwtProvider.getClaims(command.refreshToken()))
                .willReturn(rtClaims);
        given(jwtProvider.getJti(rtClaims))
                .willReturn(rtJti);
        given(refreshTokenRepository.existsByJti(rtJti))
                .willReturn(true);
        given(jwtProvider.getUserId(rtClaims))
                .willReturn(userId);
        given(userService.getActiveUser(userId))
                .willReturn(user);
        given(user.isAdmin())
                .willReturn(false);
        given(jwtProvider.getClaimsIgnoreExpiration(command.accessToken()))
                .willReturn(atClaims);
        given(atClaims.getSubject())
                .willReturn(String.valueOf(userId));
        given(rtClaims.getSubject())
                .willReturn(String.valueOf(userId));
        given(jwtProvider.createAccessToken(userId, user.isAdmin()))
                .willReturn(newAccessToken);
        given(jwtProvider.createRefreshToken(userId))
                .willReturn(newRefreshToken);

        // when
        TokenReissueResult result = tokenReissueUseCase.execute(command);

        // then
        assertThat(result.accessToken())
                .isEqualTo(newAccessToken.token());
        assertThat(result.refreshToken())
                .isEqualTo(newRefreshToken.token());

        then(refreshTokenRepository)
                .should()
                .save(newRefreshToken.jti(), newRefreshToken.token(), newRefreshToken.expirationMillis());
        then(refreshTokenRepository)
                .should()
                .delete(rtJti);
    }

    @Test
    @DisplayName("저장된 리프레시 토큰이 존재하지 않으면 예외가 발생한다.")
    void execute_WhenRefreshTokenNotExists_ThrowsException() {
        // given
        TokenReissueCommand command = new TokenReissueCommand("accessToken", "refreshToken");
        Claims rtClaims = mock(Claims.class);
        String rtJti = "jti";

        given(jwtProvider.getClaims(command.refreshToken()))
                .willReturn(rtClaims);
        given(jwtProvider.getJti(rtClaims))
                .willReturn(rtJti);
        given(refreshTokenRepository.existsByJti(rtJti))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> tokenReissueUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("액세스 토큰의 서브젝트와 리프레시 토큰의 서브젝트가 일치하지 않으면 예외가 발생한다.")
    void execute_WhenMismatchSubject_ThrowsException() {
        // given
        long userId = 1L;
        TokenReissueCommand command = new TokenReissueCommand("accessToken", "refreshToken");
        Claims rtClaims = mock(Claims.class);
        Claims atClaims = mock(Claims.class);
        String rtJti = "jti";
        User user = mock(User.class);

        given(jwtProvider.getClaims(command.refreshToken()))
                .willReturn(rtClaims);
        given(jwtProvider.getJti(rtClaims))
                .willReturn(rtJti);
        given(refreshTokenRepository.existsByJti(rtJti))
                .willReturn(true);
        given(jwtProvider.getUserId(rtClaims))
                .willReturn(userId);
        given(userService.getActiveUser(userId))
                .willReturn(user);
        given(jwtProvider.getClaimsIgnoreExpiration(command.accessToken()))
                .willReturn(atClaims);

        given(atClaims.getSubject()).willReturn("user1");
        given(rtClaims.getSubject()).willReturn("user2");

        // when & then
        assertThatThrownBy(() -> tokenReissueUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_MISMATCH);
    }
}