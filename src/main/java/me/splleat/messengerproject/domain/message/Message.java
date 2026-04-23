package me.splleat.messengerproject.domain.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.attachment.Attachment;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends SoftDeletableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Getter
    @Column(name = "content")
    private String content;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MessageType type;

    @Getter
    @Column(name = "parent_message_id")
    private Long parentMessageId;

    @Getter
    @Column(name = "idempotency_key")
    private UUID idemPotencyKey;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    @Builder
    private Message(User user, Channel channel, String content, MessageType type, Long parentMessageId, UUID idemPotencyKey, List<Attachment> attachments) {
        this.user = user;
        this.channel = channel;
        this.content = content;
        this.type = type;
        this.parentMessageId = parentMessageId;
        this.idemPotencyKey = idemPotencyKey;
        this.attachments = attachments;
    }

    public static Message create(User user, Channel channel, String content, MessageType type, Long parentMessageId, UUID idemPotencyKey, List<Attachment> attachments) {
        return Message.builder()
                .user(user)
                .channel(channel)
                .content(content)
                .type(type)
                .parentMessageId(parentMessageId)
                .idemPotencyKey(idemPotencyKey)
                .attachments(attachments)
                .build();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getChannelId() {
        return channel.getId();
    }
}
