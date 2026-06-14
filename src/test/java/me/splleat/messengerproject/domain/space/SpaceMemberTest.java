package me.splleat.messengerproject.domain.space;

import me.splleat.messengerproject.domain.space.exception.SpaceMemberNotPermittedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SpaceMemberTest {

    @Test
    @DisplayName("그룹 권한이 부족한 경우 예외를 던진다.")
    void validatePermission_WhenNotPermittedRole_ThrowsException() {
        // given
        long userId = 1L;
        long spaceId = 1L;
        SpaceMember spaceMember = SpaceMember.create(userId, spaceId, "test", SpaceRole.MEMBER);

        // when & then
        assertThatThrownBy(() -> spaceMember.validatePermission(SpaceRole.ADMIN))
                .isInstanceOf(SpaceMemberNotPermittedException.class);
    }

    @Test
    @DisplayName("충분한 그룹 권한이 있는 경우 예외가 발생하지 않는다.")
    void validatePermission_WhenPermittedRole_DoesNotThrowException() {
        // given
        long userId = 1L;
        long spaceId = 1L;
        SpaceMember spaceMember = SpaceMember.create(userId, spaceId, "test", SpaceRole.ADMIN);

        // when & then
        assertDoesNotThrow(() -> spaceMember.validatePermission(SpaceRole.MEMBER));
    }
}
