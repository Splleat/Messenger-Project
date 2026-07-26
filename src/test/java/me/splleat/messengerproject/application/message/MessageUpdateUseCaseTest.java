package me.splleat.messengerproject.application.message;

import me.splleat.messengerproject.application.message.dto.MessageUpdateCommand;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MessageUpdateUseCaseTest {

    @Mock
    private MessageService messageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MessageUpdateUseCase messageUpdateUseCase;

    @Test
    @DisplayName("메시지가 수정되면, 내용이 수정되고 수정 이벤트가 발생한다.")
    void execute_WhenMessageUpdated_UpdatesContentAndPublishesUpdateEvent() {
        // given
        // given
        long userId = 1L;
        long messageId = 1L;
        Message message = mock(Message.class);
        MessageUpdateCommand command = new MessageUpdateCommand(userId, messageId, "test");

        given(message.getId())
                .willReturn(messageId);
        given(messageService.getUserMessage(userId, messageId))
                .willReturn(message);

        // when
        messageUpdateUseCase.execute(command);

        // then
        then(message)
                .should()
                .update(command.content());

        then(eventPublisher)
                .should()
                .publishEvent(any(MessageEvent.class));
    }

}