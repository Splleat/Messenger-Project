package me.splleat.messengerproject.application.typing;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.typing.dto.TypingCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import org.springframework.context.ApplicationEventPublisher;

@UseCase
@RequiredArgsConstructor
public class SendTypingUseCase {
    private final UserProfileService userProfileService;
    private final ApplicationEventPublisher publisher;

    public void execute(TypingCommand command) {
        UserProfile profile = userProfileService.getUserProfile(command.userId());

        publisher.publishEvent(command.toEvent(profile.getName()));
    }
}
