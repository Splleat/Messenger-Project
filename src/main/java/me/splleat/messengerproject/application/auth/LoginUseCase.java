package me.splleat.messengerproject.application.auth;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.command.LoginCommand;
import me.splleat.messengerproject.application.result.LoginResult;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserRole;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.global.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.security.JwtProvider;
import me.splleat.messengerproject.infrastructure.security.RefreshTokenRepository;
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
        UserRole userRole = user.getRole();

        UserProfile userProfile = userProfileService.getUserProfile(userId);

        String accessToken = jwtProvider.createAccessToken(userId, userRole);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        refreshTokenRepository.save(userId, refreshToken);

        return LoginResult.of(accessToken, refreshToken, userId, userProfile);
    }
}
