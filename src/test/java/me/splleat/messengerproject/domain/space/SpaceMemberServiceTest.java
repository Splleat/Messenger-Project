package me.splleat.messengerproject.domain.space;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.SpaceMemberRepository;
import me.splleat.messengerproject.support.fixture.SpaceMemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SpaceMemberServiceTest {

    @Mock
    private SpaceMemberRepository spaceMemberRepository;

    @InjectMocks
    private SpaceMemberService spaceMemberService;

    @Test
    @DisplayName("이미 존재하는 그룹 멤버를 등록하려 하면, SpaceMemberAlreadyExistsException이 발생한다.")
    void register_WhenExists_ThrowsException() {
        // given
        long userId = 1L;
        long spaceId = 1L;

        SpaceMember spaceMember = SpaceMember.create(userId, spaceId, "test", SpaceRole.MEMBER);

        given(spaceMemberRepository.existsByUserIdAndSpaceId(spaceMember.getUserId(), spaceMember.getSpaceId()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> spaceMemberService.register(spaceMember))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SPACE_MEMBER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("존재하지 않는 그룹 멤버를 조회하려 하면, SpaceMemberNotFoundException이 발생한다.")
    void getSpaceMember_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long spaceId = 1L;

        given(spaceMemberRepository.findByUserIdAndSpaceId(userId, spaceId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> spaceMemberService.getSpaceMember(userId, spaceId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SPACE_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("멤버가 존재하지 않는 그룹의 사용자 아이디 목록을 조회하면, 빈 리스트가 반환된다.")
    void getAllParticipantUserIds_WhenEmpty_ReturnsEmptyList() {
        // when
        List<Long> userIdList = spaceMemberService.getAllParticipantUserIds(1L);

        // then
        assertThat(userIdList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹 멤버가 나가려고 하면, SpaceMemberNotFoundException이 발생한다.")
    void leaveSpace_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long spaceId = 1L;

        given(spaceMemberRepository.findByUserIdAndSpaceId(userId, spaceId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> spaceMemberService.leaveSpace(userId, spaceId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SPACE_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("그룹장이 나가려고 할 때, 다른 그룹 멤버가 존재하면 SpaceOwnerLeaveException이 발생한다.")
    void leaveSpace_WhenSpaceOwnerAndExistsAnotherSpaceMember_ThrowsException() {
        // given
        long userId = 1L;
        long spaceId = 1L;
        SpaceMember given = SpaceMemberFixture.spaceOwner(userId, spaceId);

        given(spaceMemberRepository.findByUserIdAndSpaceId(userId, spaceId))
                .willReturn(Optional.of(given));
        given(spaceMemberRepository.existsBySpaceIdAndUserIdNot(spaceId, userId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> spaceMemberService.leaveSpace(userId, spaceId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SPACE_OWNER_CANNOT_LEAVE);
    }

    @Test
    @DisplayName("그룹 참여자가 아니라면, SpaceMemberNotFoundException이 발생한다.")
    void validateParticipant_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long spaceId = 1L;

        given(spaceMemberRepository.existsByUserIdAndSpaceId(userId, spaceId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> spaceMemberService.validateParticipant(userId, spaceId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SPACE_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 참여 중인 사용자 목록을 빈 리스트로 조회하면, 빈 리스트가 반환된다.")
    void getAlreadyJoinedUserIds_WhenEmptyList_ReturnsEmptyList() {
        // given
        long spaceId = 1L;
        List<Long> userIds = List.of();

        // when
        List<Long> found = spaceMemberService.getAlreadyJoinedUserIds(spaceId, userIds);

        // then
        assertThat(found)
                .isEmpty();
    }

    @Test
    @DisplayName("이미 참여 중인 사용자 목록을 조회하면, 해당 그룹에 속한 사용자 ID 목록이 반환된다.")
    void getAlreadyJoinedUserIds_WhenCalled_ReturnsJoinedUserIds() {
        // given
        long spaceId = 1L;
        List<Long> userIds = List.of(1L, 2L, 3L);
        List<Long> joinedUserIds = List.of(1L, 2L);

        given(spaceMemberRepository.findAllUserIdBySpaceIdAndUserIdIn(spaceId, userIds))
                .willReturn(joinedUserIds);

        // when
        List<Long> found = spaceMemberService.getAlreadyJoinedUserIds(spaceId, userIds);

        // then
        assertThat(found)
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(joinedUserIds);
    }
}
