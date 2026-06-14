package me.splleat.messengerproject.infrastructure.message.relay;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.DistributedLock;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import me.splleat.messengerproject.infrastructure.message.publisher.RedisPublisher;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageOutboxRepository;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageRelayScheduler {
    private final MessageOutboxRepository messageOutboxRepository;
    private final RedisPublisher redisPublisher;

    @DistributedLock(key = "relay")
    @Scheduled(fixedDelay = 1000)
    public void relay() {
        LocalDateTime limit = LocalDateTime.now().plusMinutes(1);

        List<MessageOutbox> outboxes = messageOutboxRepository.findAllByProcessedFalseAndCreatedAtBefore(limit);

        outboxes.forEach(o -> redisPublisher.publish(o.getPayload()));
    }
}
