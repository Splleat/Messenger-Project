package me.splleat.messengerproject.infrastructure.message.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageOutboxService {
    private final MessageOutboxRepository messageOutboxRepository;

    @Transactional
    public void saveOutbox(MessageOutbox messageOutbox) {
        messageOutboxRepository.save(messageOutbox);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToProcessed(long aggregateId) {
        messageOutboxRepository.findByMessageIdAndProcessedFalse(aggregateId)
                .ifPresentOrElse(
                        MessageOutbox::complete,
                        () -> log.warn("메시지 아웃박스 엔티티를 찾을 수 없음 | id: {}", aggregateId)
                );
    }
}
