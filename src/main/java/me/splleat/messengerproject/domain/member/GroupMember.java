package me.splleat.messengerproject.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.member.exception.GroupMemberNotPermittedException;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseEntity {

    @Getter
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private GroupRole role;

    public Long getGroupId() {
        return group.getId();
    }

    @Builder
    private GroupMember(Long userId, Group group, String nickname, GroupRole role) {
        this.userId = userId;
        this.group = group;
        this.nickname = nickname;
        this.role = role;
    }

    public static GroupMember create(Long userId, Group group, String nickname, GroupRole role) {
        return new GroupMember(userId, group, nickname, role);
    }

    public void validatePermission(GroupRole requiredRole) {
        if (!role.hasPermission(requiredRole)) {
            throw new GroupMemberNotPermittedException();
        }
    }
}
