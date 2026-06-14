package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageOutboxRepository extends JpaRepository<MessageOutbox, Long> {
    Optional<MessageOutbox> findByMessageIdAndProcessedFalse(long messageId);

    List<MessageOutbox> findTop500ByProcessedFalseAndCreatedAtBetweenOrderByIdAsc(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
