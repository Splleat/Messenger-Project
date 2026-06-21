package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "channel_user_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_channel_user_settings_user_channel", columnNames = {"user_id", "channel_id"})
        },
        indexes = {
                @Index(name = "idx_channel_user_settings_channel_id", columnList = "channel_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelUserSetting extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "is_pinned")
    private boolean isPinned;

    @Column(name = "is_muted")
    private boolean isMuted;

    @Builder
    private ChannelUserSetting(Long userId, Long channelId) {
        this.userId = userId;
        this.channelId = channelId;
        isPinned = false;
        isMuted = false;
    }

    public static ChannelUserSetting create(Long userId, Long channelId) {
        return ChannelUserSetting.builder()
                .userId(userId)
                .channelId(channelId)
                .build();
    }

    public void updateLastReadMessage(Long lastReadMessageId) {
        if (lastReadMessageId != null && (this.lastReadMessageId == null || lastReadMessageId > this.lastReadMessageId)) {
            this.lastReadMessageId = lastReadMessageId;
        }
    }
}
