package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "channel_user_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelUserSetting extends BaseEntity {

    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Getter
    @Column(name = "channel_id")
    private Long channelId;

    @Getter
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
}
