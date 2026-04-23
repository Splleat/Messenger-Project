package me.splleat.messengerproject.application.auth;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.auth.dto.LoginCommand;
import me.splleat.messengerproject.application.auth.dto.LoginResult;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
import me.splleat.messengerproject.infrastructure.security.dto.TokenResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResult execute(LoginCommand command) {
        User user = userService.getUser(command.email());

        user.login(command.password(), passwordEncoder);

        Long userId = user.getId();
        boolean isAdmin = user.isAdmin();

        UserProfile userProfile = userProfileService.getUserProfile(userId);

        String accessToken = getAccessToken(userId, isAdmin);
        String refreshToken = getRefreshToken(userId);

        return LoginResult.of(accessToken, refreshToken, userId, userProfile);
    }

    private String getAccessToken(long userId, boolean isAdmin) {
        TokenResult accessTokenResult = jwtProvider.createAccessToken(userId, isAdmin);

        return accessTokenResult.token();
    }

    private String getRefreshToken(long userId) {
        TokenResult refreshTokenResult = jwtProvider.createRefreshToken(userId);

        refreshTokenRepository.save(refreshTokenResult.jti(), refreshTokenResult.token(), refreshTokenResult.expirationMillis());

        return refreshTokenResult.token();
    }
}
