package me.splleat.messengerproject.application.auth;

import io.jsonwebtoken.Claims;
import me.splleat.messengerproject.application.auth.dto.LogoutCommand;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.infrastructure.security.BlacklistTokenRepository;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private BlacklistTokenRepository blacklistTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @Test
    @DisplayName("액세스 토큰을 블랙리스트에 등록하고, 등록된 리프레시 토큰을 삭제한다.")
    void execute_WhenCalled_RegistersAccessTokenToBlacklistAndRemoveRefreshToken() {
        // given
        String jti = "jti";
        long expirationMillis = System.currentTimeMillis() + 60 * 60 * 60;
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        LogoutCommand command = new LogoutCommand(accessToken, refreshToken);

        Claims mockAccessClaims = mock(Claims.class);
        Claims mockRefreshClaims = mock(Claims.class);

        given(jwtProvider.getClaims(accessToken))
                .willReturn(mockAccessClaims);
        given(jwtProvider.getClaims(refreshToken))
                .willReturn(mockRefreshClaims);
        given(jwtProvider.getExpiration(mockAccessClaims))
                .willReturn(expirationMillis);
        given(jwtProvider.getJti(mockAccessClaims))
                .willReturn(jti);
        given(jwtProvider.getJti(mockRefreshClaims))
                .willReturn(jti);

        // when
        assertDoesNotThrow(() -> logoutUseCase.execute(command));

        // then
        then(blacklistTokenRepository)
                .should()
                .save(eq(jti), anyLong());

        then(refreshTokenRepository)
                .should()
                .delete(jti);
    }

    @Test
    @DisplayName("액세스 토큰 파싱 중 예외가 발생했다면, 리프레시 토큰만 삭제한다.")
    void execute_WhenParingFailedAccessToken_DeletesOnlyRefreshToken() {
        // given
        String jti = "jti";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        LogoutCommand command = new LogoutCommand(accessToken, refreshToken);

        Claims mockRefreshClaims = mock(Claims.class);

        given(jwtProvider.getClaims(accessToken))
                .willThrow(BusinessException.class);
        given(jwtProvider.getClaims(refreshToken))
                .willReturn(mockRefreshClaims);
        given(jwtProvider.getJti(mockRefreshClaims))
                .willReturn(jti);

        // when
        assertDoesNotThrow(() -> logoutUseCase.execute(command));

        // then
        then(blacklistTokenRepository)
                .should(never())
                .save(anyString(), anyLong());

        then(refreshTokenRepository)
                .should()
                .delete(jti);
    }

    @Test
    @DisplayName("리프레시 토큰 파싱 중 예외가 발생했다면, 액세스 토큰만 블랙리스트에 등록된다.")
    void execute_WhenParsingFailedRefreshToken_RegistersOnlyBlacklist() {
        // given
        String jti = "jti";
        long expirationMillis = System.currentTimeMillis() + 60 * 60 * 60;
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        LogoutCommand command = new LogoutCommand(accessToken, refreshToken);

        Claims mockAccessClaims = mock(Claims.class);

        given(jwtProvider.getClaims(accessToken))
                .willReturn(mockAccessClaims);
        given(jwtProvider.getExpiration(mockAccessClaims))
                .willReturn(expirationMillis);
        given(jwtProvider.getJti(mockAccessClaims))
                .willReturn(jti);
        given(jwtProvider.getClaims(refreshToken))
                .willThrow(BusinessException.class);

        // when
        assertDoesNotThrow(() -> logoutUseCase.execute(command));

        // then
        then(blacklistTokenRepository)
                .should()
                .save(eq(jti), anyLong());

        then(refreshTokenRepository)
                .should(never())
                .delete(anyString());
    }

    @Test
    @DisplayName("액세스 토큰과 리프레시 토큰 모두 파싱 중 예외가 발생했다면, 아무 일도 발생하지 않는다.")
    void execute_WhenEveryTokenOccurException_DoesNothing() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        LogoutCommand command = new LogoutCommand(accessToken, refreshToken);

        given(jwtProvider.getClaims(accessToken))
                .willThrow(BusinessException.class);
        given(jwtProvider.getClaims(refreshToken))
                .willThrow(BusinessException.class);

        // when
        assertDoesNotThrow(() -> logoutUseCase.execute(command));

        // then
        then(blacklistTokenRepository)
                .should(never())
                .save(anyString(), anyLong());

        then(refreshTokenRepository)
                .should(never())
                .delete(anyString());
    }
}