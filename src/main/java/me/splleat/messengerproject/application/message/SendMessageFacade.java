package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.metric.MessageMetricRecorder;
import me.splleat.messengerproject.infrastructure.storage.S3Service;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.resilience.annotation.Retryable;

@UseCase
@RequiredArgsConstructor
public class SendMessageFacade {
    private final S3Service s3Service;
    private final SendMessageUseCase sendMessageUseCase;
    private final MessageMetricRecorder messageMetricRecorder;

    @Retryable(
            includes = {DataIntegrityViolationException.class, TransientDataAccessException.class},
            maxRetries = 1
    )
    public MessageResponse execute(MessageCreateCommand command) {
        return messageMetricRecorder.recordSendDuration(() -> {
            // 첨부파일이 있는 경우, DB에 저장하기 이전 S3 스토리지 파일 용량 검증
            if (command.attachments() != null && !command.attachments().isEmpty()) {
                command.attachments().forEach(req -> s3Service.validateObjectSize(req.url()));
            }

            return sendMessageUseCase.execute(command);
        });
    }
}
