package me.splleat.messengerproject.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "channel_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends SoftDeletableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private MemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message lastReadMessage;

    @Column(name = "is_pinned")
    private boolean isPinned;

    @Column(name = "is_muted")
    private boolean isMuted;

    @Builder
    private Member(User user, Channel channel, String nickname, MemberRole role) {
        this.user = user;
        this.channel = channel;
        this.nickname = nickname;
        this.role = role;
        isPinned = false;
        isMuted = false;
    }

    public static Member create(User user, Channel channel, String nickname, MemberRole role) {
        return Member.builder()
                .user(user)
                .channel(channel)
                .nickname(nickname)
                .role(role)
                .build();
    }
}
