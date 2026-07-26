package me.splleat.messengerproject.application.message;

import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.domain.message.MessageType;
import me.splleat.messengerproject.infrastructure.metric.MessageMetricRecorder;
import me.splleat.messengerproject.infrastructure.storage.S3Service;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SendMessageFacadeTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private SendMessageUseCase sendMessageUseCase;

    @Mock
    private MessageMetricRecorder messageMetricRecorder;

    @InjectMocks
    private SendMessageFacade sendMessageFacade;

    @Test
    @DisplayName("메시지를 전송하면, 전송 처리 시간을 계측기로 감싸서 실행한다.")
    void execute_WhenSendMessage_RecordsSendDuration() {
        // given
        MessageCreateCommand command = new MessageCreateCommand(1L, 1L, "test", UUID.randomUUID(), MessageType.DIRECT, null, null);
        MessageResponse response = mock(MessageResponse.class);

        given(sendMessageUseCase.execute(command))
                .willReturn(response);
        given(messageMetricRecorder.recordSendDuration(any()))
                .willAnswer(invocation -> invocation.<Supplier<MessageResponse>>getArgument(0).get());

        // when
        MessageResponse result = sendMessageFacade.execute(command);

        // then
        assertThat(result).isEqualTo(response);
        then(messageMetricRecorder).should().recordSendDuration(any());
    }
}
