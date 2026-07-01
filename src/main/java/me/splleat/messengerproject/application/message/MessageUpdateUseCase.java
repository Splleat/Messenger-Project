package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageUpdateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import me.splleat.messengerproject.infrastructure.message.event.MessageUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class MessageUpdateUseCase {
    private final MessageService messageService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void execute(MessageUpdateCommand command) {
        Message message = messageService.getUserMessage(command.userId(), command.messageId());

        message.update(command.content());

        publisher.publishEvent(MessageEvent.from(MessageUpdatedEvent.from(message)));
    }
}
