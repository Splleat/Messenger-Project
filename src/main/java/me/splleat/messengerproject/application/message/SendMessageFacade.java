package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.resilience.annotation.Retryable;

@UseCase
@RequiredArgsConstructor
public class SendMessageFacade {
    private final SendMessageUseCase sendMessageUseCase;

    @Retryable(
            includes = {DataIntegrityViolationException.class, TransientDataAccessException.class},
            maxRetries = 1
    )
    public MessageResponse execute(MessageCreateCommand command) {
        return sendMessageUseCase.execute(command);
    }
}
