package me.splleat.messengerproject.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.member.exception.GroupMemberNotPermittedException;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private GroupRole role;

    public Long getUserId() {
        return user.getId();
    }

    public Long getGroupId() {
        return group.getId();
    }

    @Builder
    private GroupMember(User user, Group group, String nickname, GroupRole role) {
        this.user = user;
        this.group = group;
        this.nickname = nickname;
        this.role = role;
    }

    public static GroupMember create(User user, Group group, String nickname, GroupRole role) {
        return new GroupMember(user, group, nickname, role);
    }

    public void validatePermission(GroupRole requiredRole) {
        if (!role.hasPermission(requiredRole)) {
            throw new GroupMemberNotPermittedException();
        }
    }
}
