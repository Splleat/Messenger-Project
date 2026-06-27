package me.splleat.messengerproject.domain.space;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "space_members", uniqueConstraints = {
        @UniqueConstraint(name = "uq_space_members_user_space", columnNames = {"user_id", "space_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpaceMember extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "space_id")
    private Long spaceId;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private SpaceRole role;

    @Builder
    private SpaceMember(Long userId, Long spaceId, String nickname, SpaceRole role) {
        this.userId = userId;
        this.spaceId = spaceId;
        this.nickname = nickname;
        this.role = role;
    }

    public static SpaceMember create(Long userId, Long spaceId, String nickname, SpaceRole role) {
        return new SpaceMember(userId, spaceId, nickname, role);
    }

    public void validatePermission(SpaceRole requiredRole) {
        if (!role.hasPermission(requiredRole)) {
            throw new BusinessException(ErrorCode.SPACE_MEMBER_NOT_PERMITTED);
        }
    }

    public boolean isSpaceOwner() {
        return role == SpaceRole.OWNER;
    }
}
