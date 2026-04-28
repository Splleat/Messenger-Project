package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
        SELECT m
        FROM Message m
        WHERE m.channel.id = :channelId AND m.id > 
    """)
    List<Message> findAllByChannelId(@Param("channelId") Long channelId);

    Message findByIdemPotencyKey(UUID idemPotencyKey);
}
