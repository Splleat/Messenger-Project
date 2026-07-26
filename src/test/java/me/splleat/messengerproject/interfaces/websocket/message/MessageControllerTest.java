package me.splleat.messengerproject.interfaces.websocket.message;

import me.splleat.messengerproject.application.message.MessageDeleteUseCase;
import me.splleat.messengerproject.application.message.MessageUpdateUseCase;
import me.splleat.messengerproject.application.message.SendMessageFacade;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.application.message.dto.MessageUpdateCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCreateRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageUpdateRequest;
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
class MessageControllerTest {

    @Mock
    private SendMessageFacade sendMessageFacade;

    @Mock
    private MessageUpdateUseCase messageUpdateUseCase;

    @Mock
    private MessageDeleteUseCase messageDeleteUseCase;

    @InjectMocks
    private MessageController messageController;

    @Test
    @DisplayName("메시지 전송 요청 시, MessageFacade를 통해 메시지를 전송한다.")
    void sendMessage_ExecutesFacade() {
        // given
        long channelId = 1L;
        long userId = 2L;
        MessageCreateRequest request = mock(MessageCreateRequest.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        MessageCreateCommand command = mock(MessageCreateCommand.class);

        given(userPrincipal.getUserId())
                .willReturn(userId);
        given(request.toCommand(userId, channelId))
                .willReturn(command);

        // when
        messageController.sendMessage(channelId, request, userPrincipal);

        // then
        then(sendMessageFacade)
                .should()
                .execute(command);
    }

    @Test
    @DisplayName("메시지 수정 요청 시, 유스케이스를 통해 메시지를 수정한다.")
    void updateMessage_ExecutesUseCase() {
        // given
        long channelId = 1L;
        long messageId = 2L;
        long userId = 3L;
        MessageUpdateRequest request = mock(MessageUpdateRequest.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        MessageUpdateCommand command = mock(MessageUpdateCommand.class);

        given(userPrincipal.getUserId()).willReturn(userId);
        given(request.toCommand(userId, messageId)).willReturn(command);

        // when
        messageController.updateMessage(channelId, messageId, request, userPrincipal);

        // then
        then(messageUpdateUseCase).should().execute(command);
    }

    @Test
    @DisplayName("메시지 삭제 요청 시, 유스케이스를 통해 메시지를 삭제한다.")
    void deleteMessage_ExecutesUseCase() {
        // given
        long channelId = 1L;
        long messageId = 2L;
        long userId = 3L;
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        given(userPrincipal.getUserId())
                .willReturn(userId);

        // when
        messageController.deleteMessage(channelId, messageId, userPrincipal);

        // then
        then(messageDeleteUseCase)
                .should()
                .execute(userId, messageId);
    }
}
