package me.splleat.messengerproject.domain.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_channel_id", columnList = "channel_id, id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends SoftDeletableEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MessageType type;

    @Column(name = "parent_message_id")
    private Long parentMessageId;

    @Column(name = "idempotency_key", unique = true)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idemPotencyKey;

    @Builder
    private Message(Long userId, Long channelId, String content, MessageType type, Long parentMessageId, UUID idemPotencyKey) {
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        this.type = type;
        this.parentMessageId = parentMessageId;
        this.idemPotencyKey = idemPotencyKey;
    }

    public static Message create(long userId, long channelId, String content, MessageType type, Long parentMessageId, UUID idemPotencyKey) {
        return Message.builder()
                .userId(userId)
                .channelId(channelId)
                .content(content)
                .type(type)
                .parentMessageId(parentMessageId)
                .idemPotencyKey(idemPotencyKey)
                .build();
    }
}
