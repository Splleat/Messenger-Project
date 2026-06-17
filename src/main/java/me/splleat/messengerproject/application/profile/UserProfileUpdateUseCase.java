package me.splleat.messengerproject.application.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.dto.UserProfileUpdateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.user.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class UserProfileUpdateUseCase {
    private final UserProfileService userProfileService;

    @Transactional
    public void execute(UserProfileUpdateCommand command) {
        userProfileService.updateProfile(command.userId(), command.name(), command.statusMessage());
    }
}
