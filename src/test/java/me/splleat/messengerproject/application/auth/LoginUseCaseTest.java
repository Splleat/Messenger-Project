package me.splleat.messengerproject.application.auth;

import me.splleat.messengerproject.application.command.LoginCommand;
import me.splleat.messengerproject.application.result.LoginResult;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserRole;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
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
    private UserService mockUserService;

    @Mock
    private UserProfileService mockUserProfileService;

    @Mock
    private JwtProvider mockJwtProvider;

    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 토큰과 프로필 정보를 반환한다.")
    void execute_WhenValidCredentials_ReturnsLoginResult() {
        // given
        String password = "password123";

        LoginCommand command = new LoginCommand("test@test.com", password);
        User user = mock(User.class);

        Long userId = 1L;

        given(user.getId())
                .willReturn(userId);
        given(user.getRole())
                .willReturn(UserRole.USER);

        UserProfile userProfile = mock(UserProfile.class);
        given(userProfile.getName())
                .willReturn("test");
        given(userProfile.getImageUrl())
                .willReturn("testImage");
        given(userProfile.getStatusMessage())
                .willReturn("testStatusMessage");

        given(mockUserService.getUser(command.email()))
                .willReturn(user);
        given(mockUserProfileService.getUserProfile(userId))
                .willReturn(userProfile);
        given(mockJwtProvider.createAccessToken(userId, user.getRole()))
                .willReturn("accessToken");
        given(mockJwtProvider.createRefreshToken(userId))
                .willReturn("refreshToken");

        // when
        LoginResult result = loginUseCase.execute(command);

        // then
        assertThat(result.accessToken())
                .isEqualTo("accessToken");

        assertThat(result.refreshToken())
                .isEqualTo("refreshToken");

        then(mockRefreshTokenRepository)
                .should()
                .save(userId, result.refreshToken());

        then(user)
                .should()
                .login(password, mockPasswordEncoder);
    }
}