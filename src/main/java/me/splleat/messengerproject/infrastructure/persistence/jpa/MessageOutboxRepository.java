package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageOutboxRepository extends JpaRepository<MessageOutbox, Long> {
    Optional<MessageOutbox> findByMessageIdAndProcessedFalse(long messageId);

    List<MessageOutbox> findTop500ByProcessedFalseAndCreatedAtBetweenOrderByIdAsc(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    @Modifying
    @Query("""
        UPDATE MessageOutbox o
        SET o.processed = true
        WHERE o.id IN :ids
    """)
    void updateProcessedStatusIdIn(@Param("ids") List<Long> ids);
}
