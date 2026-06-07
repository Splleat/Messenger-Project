package me.splleat.messengerproject.application.auth;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.auth.dto.RegisterCommand;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.common.annotation.UseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class RegisterUseCase {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(RegisterCommand command) {
        String encryptedPassword = passwordEncoder.encode(command.password());

        User user = User.create(command.email(), encryptedPassword, false);

        User savedUser = userService.register(user);

        UserProfile profile = UserProfile.create(savedUser.getId(), command.name());

        userProfileService.register(profile);
    }
}
