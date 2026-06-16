package me.splleat.messengerproject.infrastructure.message.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageOutboxService {
    private final MessageOutboxRepository messageOutboxRepository;

    @Transactional
    public void saveOutbox(MessageOutbox messageOutbox) {
        messageOutboxRepository.save(messageOutbox);
    }

    @Transactional
    public void updateToProcessed(long aggregateId) {
        messageOutboxRepository.findByMessageIdAndProcessedFalse(aggregateId)
                .ifPresentOrElse(
                        MessageOutbox::complete,
                        () -> log.warn("메시지 아웃박스 엔티티를 찾을 수 없음 | id: {}", aggregateId)
                );
    }

    @Transactional(readOnly = true)
    public List<MessageOutbox> getMessageOutboxInWindow(LocalDateTime after, LocalDateTime before) {
        return messageOutboxRepository.findTop500ByProcessedFalseAndCreatedAtBetweenOrderByIdAsc(after, before);
    }

    @Transactional
    public void updateToProcessedInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        messageOutboxRepository.updateProcessedStatusIdIn(ids);
    }
}
