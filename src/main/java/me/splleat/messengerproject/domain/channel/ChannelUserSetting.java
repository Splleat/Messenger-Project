package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "channel_user_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelUserSetting extends BaseEntity {

    @Getter
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "is_pinned")
    private boolean isPinned;

    @Column(name = "is_muted")
    private boolean isMuted;

    public Long getChannelId() {
        return channel.getId();
    }

    @Builder
    private ChannelUserSetting(Long userId, Channel channel) {
        this.userId = userId;
        this.channel = channel;
        isPinned = false;
        isMuted = false;
    }

    public static ChannelUserSetting create(Long userId, Channel channel) {
        return ChannelUserSetting.builder()
                .userId(userId)
                .channel(channel)
                .build();
    }
}
