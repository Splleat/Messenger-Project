package me.splleat.messengerproject.domain.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.attachment.Attachment;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "idempotency_key")
    private UUID idemPotencyKey;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    @Builder
    private Message(Long userId, Long channelId, String content, MessageType type, Long parentMessageId, UUID idemPotencyKey, List<Attachment> attachments) {
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        this.type = type;
        this.parentMessageId = parentMessageId;
        this.idemPotencyKey = idemPotencyKey;
        this.attachments = attachments;
    }

    public static Message create(long userId, long channelId, String content, MessageType type, Long parentMessageId, UUID idemPotencyKey, List<Attachment> attachments) {
        return Message.builder()
                .userId(userId)
                .channelId(channelId)
                .content(content)
                .type(type)
                .parentMessageId(parentMessageId)
                .idemPotencyKey(idemPotencyKey)
                .attachments(attachments)
                .build();
    }
}
