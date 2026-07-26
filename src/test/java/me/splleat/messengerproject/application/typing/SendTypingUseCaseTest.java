package me.splleat.messengerproject.application.typing;

import me.splleat.messengerproject.application.typing.dto.TypingCommand;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SendTypingUseCaseTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private SendTypingUseCase sendTypingUseCase;

    @Test
    @DisplayName("입력 중인 사용자의 이름을 포함한 타이핑 이벤트를 발행한다.")
    void execute_PublishesTypingEvent() {
        // given
        long userId = 1L;
        String userName = "splleat";
        TypingCommand command = mock(TypingCommand.class);
        UserProfile profile = mock(UserProfile.class);

        given(command.userId())
                .willReturn(userId);
        given(userProfileService.getUserProfile(userId))
                .willReturn(profile);
        given(profile.getName())
                .willReturn(userName);

        // when
        sendTypingUseCase.execute(command);

        // then
        then(userProfileService)
                .should()
                .getUserProfile(userId);
        then(command)
                .should()
                .toEvent(userName);
        then(publisher)
                .should()
                .publishEvent(command.toEvent(userName));
    }
}
