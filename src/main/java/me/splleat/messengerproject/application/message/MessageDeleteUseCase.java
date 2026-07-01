package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageDeleteCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.infrastructure.message.event.MessageDeletedEvent;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class MessageDeleteUseCase {
    private final MessageService messageService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void execute(MessageDeleteCommand command) {
        Message message = messageService.getUserMessage(command.userId(), command.messageId());

        message.softDelete();

        publisher.publishEvent(MessageEvent.from(MessageDeletedEvent.from(message)));
    }
}
