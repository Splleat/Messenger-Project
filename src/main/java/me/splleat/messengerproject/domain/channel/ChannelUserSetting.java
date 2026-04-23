package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "channel_user_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelUserSetting extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "is_pinned")
    private boolean isPinned;

    @Column(name = "is_muted")
    private boolean isMuted;

    public Long getUserId() {
        return user.getId();
    }

    public Long getChannelId() {
        return channel.getId();
    }

    @Builder
    private ChannelUserSetting(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        isPinned = false;
        isMuted = false;
    }

    public static ChannelUserSetting create(User user, Channel channel) {
        return ChannelUserSetting.builder()
                .user(user)
                .channel(channel)
                .build();
    }
}
