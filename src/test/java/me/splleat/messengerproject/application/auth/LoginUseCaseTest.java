package me.splleat.messengerproject.application.auth;

import me.splleat.messengerproject.application.auth.dto.LoginCommand;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;
import me.splleat.messengerproject.interfaces.rest.auth.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 토큰과 프로필 정보를 반환한다.")
    void execute_WhenValidCredentials_ReturnsLoginResult() {
        // given
        String jti = "jti";
        String accessToken = "accessToken";
        long expirationMillis = 1000;
        String refreshToken = "refreshToken";
        String password = "password123";

        LoginCommand command = new LoginCommand("test@test.com", password);
        User user = mock(User.class);

        Long userId = 1L;

        given(user.getId())
                .willReturn(userId);
        given(user.isAdmin())
                .willReturn(false);

        UserProfile userProfile = mock(UserProfile.class);

        given(userProfile.getName())
                .willReturn("test");
        given(userProfile.getImageUrl())
                .willReturn("testImage");
        given(userProfile.getStatusMessage())
                .willReturn("testStatusMessage");
        given(jwtProvider.createRefreshToken(userId))
                .willReturn(new TokenResult(jti, refreshToken, expirationMillis));

        given(userService.getUser(command.email()))
                .willReturn(user);
        given(userProfileService.getUserProfile(userId))
                .willReturn(userProfile);
        given(jwtProvider.createAccessToken(userId, user.isAdmin()))
                .willReturn(new TokenResult(jti, accessToken, expirationMillis));
        given(jwtProvider.createRefreshToken(userId))
                .willReturn(new TokenResult(jti, refreshToken, expirationMillis));

        // when
        LoginResponse result = loginUseCase.execute(command);

        // then
        assertThat(result.accessToken())
                .isEqualTo(accessToken);

        assertThat(result.refreshToken())
                .isEqualTo(refreshToken);

        then(refreshTokenRepository)
                .should()
                .save(jti, result.refreshToken(), expirationMillis);

        then(user)
                .should()
                .login(password, passwordEncoder);
    }
}