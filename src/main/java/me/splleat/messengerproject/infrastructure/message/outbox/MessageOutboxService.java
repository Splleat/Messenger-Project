package me.splleat.messengerproject.infrastructure.message.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageOutboxService {
    private final MessageOutboxRepository messageOutboxRepository;
    private final JsonMapper jsonMapper;

    @Transactional
    public void saveOutbox(BaseEntity entity, String eventType) {
        long aggregateId = entity.getId();
        String aggregateType = ClassUtils.getUserClass(entity).getSimpleName();
        String payload = jsonMapper.writeValueAsString(entity);

        MessageOutbox messageOutbox = MessageOutbox.create(aggregateId, aggregateType, eventType, payload);

        messageOutboxRepository.save(messageOutbox);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToProcessed(long aggregateId, String aggregateType) {
        messageOutboxRepository.findByAggregateIdAndAggregateTypeAndProcessedFalse(aggregateId, aggregateType)
                .ifPresentOrElse(
                        MessageOutbox::complete,
                        () -> log.warn("아웃박스 엔티티가 존재하지 않음 id: {}, type: {}", aggregateId, aggregateType)
                );
    }
}
