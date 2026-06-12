package me.splleat.messengerproject.infrastructure.message.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageOutboxRepository extends JpaRepository<MessageOutbox, Long> {
    Optional<MessageOutbox> findByMessageIdAndProcessedFalse(long messageId);
}
