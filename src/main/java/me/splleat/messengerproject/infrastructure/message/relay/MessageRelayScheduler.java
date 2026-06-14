package me.splleat.messengerproject.infrastructure.message.relay;

import me.splleat.messengerproject.common.annotation.DistributedLock;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import me.splleat.messengerproject.infrastructure.message.publisher.RedisPublisher;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageOutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MessageRelayScheduler {
    private final MessageOutboxRepository messageOutboxRepository;
    private final RedisPublisher redisPublisher;
    private final long graceSeconds;
    private final long giveUpSeconds;

    @Autowired
    public MessageRelayScheduler(
            MessageOutboxRepository messageOutboxRepository,
            RedisPublisher redisPublisher,
            @Value("${relay.grace-seconds:2}") long graceSeconds,
            @Value("${relay.give-up-seconds:60}") long giveUpSeconds
    ) {
        this.messageOutboxRepository = messageOutboxRepository;
        this.redisPublisher = redisPublisher;
        this.graceSeconds = graceSeconds;
        this.giveUpSeconds = giveUpSeconds;
    }

    @DistributedLock(key = "relay", throwOnFailure = false)
    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void relay() {
        LocalDateTime young = LocalDateTime.now().minusSeconds(graceSeconds);
        LocalDateTime old = LocalDateTime.now().minusSeconds(giveUpSeconds);

        List<MessageOutbox> outboxes = messageOutboxRepository.findTop500ByProcessedFalseAndCreatedAtBetweenOrderByIdAsc(old, young);

        outboxes.forEach(o -> {
            redisPublisher.publish(o.getPayload());

            o.complete();
        });
    }
}
