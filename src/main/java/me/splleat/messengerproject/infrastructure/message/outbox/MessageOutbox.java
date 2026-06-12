package me.splleat.messengerproject.infrastructure.message.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageOutbox extends BaseEntity {
    @Column(name = "aggregate_id")
    private long aggregateId;

    @Column(name = "aggregate_type")
    private String aggregateType;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "payload", length = 65535)
    private String payload;

    @Builder
    private MessageOutbox(long aggregateId, String aggregateType, String eventType, String payload, boolean processed) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.processed = processed;
    }

    public static MessageOutbox create(long aggregateId, String aggregateType, String eventType, String payload) {
        return MessageOutbox.builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .payload(payload)
                .processed(false)
                .build();
    }

    public void complete() {
        this.processed = true;
    }
}
