package me.splleat.messengerproject.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.member.exception.GroupMemberNotPermittedException;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseEntity {

    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Getter
    @Column(name = "group_id")
    private Long groupId;

    @Getter
    @Column(name = "nickname")
    private String nickname;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private GroupRole role;

    @Builder
    private GroupMember(Long userId, Long groupId, String nickname, GroupRole role) {
        this.userId = userId;
        this.groupId = groupId;
        this.nickname = nickname;
        this.role = role;
    }

    public static GroupMember create(Long userId, Long groupId, String nickname, GroupRole role) {
        return new GroupMember(userId, groupId, nickname, role);
    }

    public void validatePermission(GroupRole requiredRole) {
        if (!role.hasPermission(requiredRole)) {
            throw new GroupMemberNotPermittedException();
        }
    }

    public boolean isGroupOwner() {
        return role == GroupRole.OWNER;
    }
}
