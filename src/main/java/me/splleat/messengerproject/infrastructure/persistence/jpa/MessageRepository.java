package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByIdemPotencyKey(UUID idemPotencyKey);

    List<Message> findAllByChannelId(Long channelId);
}
