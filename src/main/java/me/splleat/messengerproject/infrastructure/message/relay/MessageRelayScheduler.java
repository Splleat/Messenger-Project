package me.splleat.messengerproject.infrastructure.message.relay;

import me.splleat.messengerproject.common.annotation.DistributedLock;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutboxService;
import me.splleat.messengerproject.infrastructure.message.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MessageRelayScheduler {
    private final MessageOutboxService messageOutboxService;
    private final MessagePublisher messagePublisher;
    private final long graceSeconds;
    private final long giveUpSeconds;

    @Autowired
    public MessageRelayScheduler(
            MessageOutboxService messageOutboxService,
            MessagePublisher messagePublisher,
            @Value("${relay.grace-seconds:2}") long graceSeconds,
            @Value("${relay.give-up-seconds:60}") long giveUpSeconds
    ) {
        this.messageOutboxService = messageOutboxService;
        this.messagePublisher = messagePublisher;
        this.graceSeconds = graceSeconds;
        this.giveUpSeconds = giveUpSeconds;
    }

    @DistributedLock(key = "relay", throwOnFailure = false)
    @Scheduled(fixedDelay = 1000)
    public void relay() {
        LocalDateTime young = LocalDateTime.now().minusSeconds(graceSeconds);
        LocalDateTime old = LocalDateTime.now().minusSeconds(giveUpSeconds);

        List<MessageOutbox> outboxes = messageOutboxService.getMessageOutboxInWindow(old, young);

        outboxes.forEach(o -> messagePublisher.publish(o.getPayload()));

        List<Long> processedIds = outboxes.stream()
                .map(MessageOutbox::getId)
                .toList();

        messageOutboxService.updateToProcessedInBatch(processedIds);
    }
}
