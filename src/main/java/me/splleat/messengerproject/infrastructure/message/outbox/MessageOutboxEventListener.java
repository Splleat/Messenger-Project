package me.splleat.messengerproject.infrastructure.message.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.message.publisher.MessagePublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageOutboxEventListener {
    private final MessageOutboxService messageOutboxService;
    private final MessagePublisher messagePublisher;
    private final JsonMapper jsonMapper;

    @EventListener
    public void handleMessageEvent(MessageEvent<?> event) {
        messageOutboxService.saveOutbox(event.toOutbox(jsonMapper));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToRedis(MessageEvent<?> event) {
        try {
            messagePublisher.publish(event.toJson(jsonMapper));

            messageOutboxService.updateToProcessed(event.messageId());
        } catch (Exception e) {
            log.warn("메시지 발행 실패: {}", e.getMessage());
        }
    }
}
