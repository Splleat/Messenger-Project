package me.splleat.messengerproject.application.message;

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
class MessageDeleteUseCaseTest {

    @Mock
    private MessageService messageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MessageDeleteUseCase messageDeleteUseCase;

    @Test
    @DisplayName("메시지를 삭제하면, 메시지의 삭제 시간이 설정되고, 삭제 이벤트가 발생한다.")
    void execute_WhenMessageDeleted_SetsDeletedAtAndPublishesDeleteEvent() {
        // given
        long userId = 1L;
        long messageId = 1L;
        Message message = mock(Message.class);

        given(message.getId())
                .willReturn(messageId);
        given(messageService.getUserMessage(userId, messageId))
                .willReturn(message);

        // when
        messageDeleteUseCase.execute(userId, messageId);

        // then
        then(message)
                .should()
                .softDelete();

        then(eventPublisher)
                .should()
                .publishEvent(any(MessageEvent.class));
    }
}