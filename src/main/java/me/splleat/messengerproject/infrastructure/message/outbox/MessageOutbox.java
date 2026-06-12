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
    @Column(name = "message_id")
    private long messageId;

    @Column(name = "channel_id")
    private long channelId;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "payload", length = 65535)
    private String payload;

    @Builder
    private MessageOutbox(long messageId, long channelId, String payload, boolean processed) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.payload = payload;
        this.processed = processed;
    }

    public static MessageOutbox create(long messageId, long channelId, String payload) {
        return MessageOutbox.builder()
                .messageId(messageId)
                .channelId(channelId)
                .payload(payload)
                .processed(false)
                .build();
    }

    public void complete() {
        this.processed = true;
    }
}
