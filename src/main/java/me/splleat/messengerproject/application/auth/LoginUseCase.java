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

        TokenResult accessToken = jwtProvider.createAccessToken(userId, isAdmin);
        TokenResult refreshToken = jwtProvider.createRefreshToken(userId);

        refreshTokenRepository.save(refreshToken.jti(), refreshToken.token(), refreshToken.expirationMillis());


        return LoginResult.of(accessToken.token(), refreshToken.token(), userId, userProfile, accessToken.expirationMillis());
    }
}
