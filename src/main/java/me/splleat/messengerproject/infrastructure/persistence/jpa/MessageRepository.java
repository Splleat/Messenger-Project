package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByIdemPotencyKey(UUID idemPotencyKey);

    List<Message> findAllByChannelId(Long channelId);

    @Query("""
        SELECT m.userId
        FROM Message m
        WHERE m.channelId = :channelId
    """)
    List<Long> findAllUserIdByChannelId(Long channelId);
}
