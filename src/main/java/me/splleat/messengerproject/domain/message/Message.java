package me.splleat.messengerproject.domain.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.attachment.Attachment;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

import java.util.List;

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

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MessageType type;

    @Column(name = "parent_message_id")
    private Long parentMessageId;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
    private List<Attachment> attachments;

    @Builder
    private Message(User user, Channel channel, String content, MessageType type) {
        this.user = user;
        this.channel = channel;
        this.content = content;
        this.type = type;
    }

    public static Message create(User user, Channel channel, String content, MessageType type) {
        return Message.builder()
                .user(user)
                .channel(channel)
                .content(content)
                .type(type)
                .build();
    }
}
