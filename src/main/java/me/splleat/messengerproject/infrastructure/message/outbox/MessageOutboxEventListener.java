package me.splleat.messengerproject.infrastructure.message.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.message.publisher.MessagePublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.ClassUtils;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageOutboxEventListener {
    private final MessageOutboxService messageOutboxService;
    private final MessagePublisher messagePublisher;
    private final JsonMapper jsonMapper;

    @EventListener
    public void handleDomainCreated(DomainCreatedEvent event) {
        messageOutboxService.saveOutbox(event.entity(), event.eventType());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToRedis(DomainCreatedEvent event) {
        try {
            messagePublisher.publish(event.toJson(jsonMapper));

            String aggregateType = ClassUtils.getUserClass(event.entity()).getSimpleName();
            messageOutboxService.updateToProcessed(event.entity().getId(), aggregateType);
        } catch (Exception e) {
            log.warn("메시지 발행 실패: {}", e.getMessage());
        }
    }
}
