package me.splleat.messengerproject.interfaces.websocket.typing;

import me.splleat.messengerproject.application.typing.SendTypingUseCase;
import me.splleat.messengerproject.application.typing.dto.TypingCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.typing.dto.TypingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TypingControllerTest {

    @Mock
    private SendTypingUseCase sendTypingUseCase;

    @InjectMocks
    private TypingController typingController;

    @Test
    @DisplayName("타이핑 이벤트 전송 요청 시, 유스케이스를 통해 이벤트를 처리한다.")
    void sendTypingEvent_ExecutesUseCase() {
        // given
        long channelId = 1L;
        long userId = 2L;
        TypingRequest request = mock(TypingRequest.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        TypingCommand command = mock(TypingCommand.class);

        given(userPrincipal.getUserId())
                .willReturn(userId);
        given(request.toCommand(userId, channelId))
                .willReturn(command);

        // when
        typingController.sendTypingEvent(channelId, request, userPrincipal);

        // then
        then(sendTypingUseCase)
                .should()
                .execute(command);
    }
}
