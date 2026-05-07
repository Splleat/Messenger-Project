package me.splleat.messengerproject.domain.member;

import me.splleat.messengerproject.domain.member.exception.GroupMemberNotPermittedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GroupMemberTest {
    
    @Test
    @DisplayName("그룹 권한이 부족한 경우 예외를 던진다.")
    void validatePermission_WhenNotPermittedRole_ThrowsException() {
        // given
        long userId = 1L;
        long groupId = 1L;
        GroupMember groupMember = GroupMember.create(userId, groupId, "test", GroupRole.MEMBER);
        
        // when & then
        assertThatThrownBy(() -> groupMember.validatePermission(GroupRole.ADMIN))
                .isInstanceOf(GroupMemberNotPermittedException.class);
    }

    @Test
    @DisplayName("충분한 그룹 권한이 있는 경우 예외가 발생하지 않는다.")
    void validatePermission_WhenPermittedRole_DoesNotThrowException() {
        // given
        long userId = 1L;
        long groupId = 1L;
        GroupMember groupMember = GroupMember.create(userId, groupId, "test", GroupRole.ADMIN);

        // when & then
        assertDoesNotThrow(() -> groupMember.validatePermission(GroupRole.MEMBER));
    }
}
