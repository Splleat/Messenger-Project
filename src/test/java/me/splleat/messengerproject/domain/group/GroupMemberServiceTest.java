package me.splleat.messengerproject.domain.group;

import me.splleat.messengerproject.domain.group.exception.GroupMemberAlreadyExistsException;
import me.splleat.messengerproject.domain.group.exception.GroupMemberNotFoundException;
import me.splleat.messengerproject.domain.group.exception.GroupOwnerLeaveException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupMemberRepository;
import me.splleat.messengerproject.support.fixture.GroupMemberFixture;
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
class GroupMemberServiceTest {

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private GroupMemberService groupMemberService;

    @Test
    @DisplayName("이미 존재하는 그룹 멤버를 등록하려 하면, GroupMemberAlreadyExistsException이 발생한다.")
    void register_WhenExists_ThrowsException() {
        // given
        long userId = 1L;
        long groupId = 1L;

        GroupMember groupMember = GroupMember.create(userId, groupId, "test", GroupRole.MEMBER);

        given(groupMemberRepository.existsByUserIdAndGroupId(groupMember.getUserId(), groupMember.getGroupId()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> groupMemberService.register(groupMember))
                .isInstanceOf(GroupMemberAlreadyExistsException.class);
    }

    @Test
    @DisplayName("존재하지 않는 그룹 멤버를 조회하려 하면, GroupMemberNotFoundException이 발생한다.")
    void getGroupMember_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long groupId = 1L;

        given(groupMemberRepository.findByUserIdAndGroupId(userId, groupId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupMemberService.getGroupMember(userId, groupId))
                .isInstanceOf(GroupMemberNotFoundException.class);
    }

    @Test
    @DisplayName("멤버가 존재하지 않는 그룹의 사용자 아이디 목록을 조회하면, 빈 리스트가 반환된다.")
    void getAllParticipantUserIds_WhenEmpty_ReturnsEmptyList() {
        // when
        List<Long> userIdList = groupMemberService.getAllParticipantUserIds(1L);

        // then
        assertThat(userIdList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹 멤버가 나가려고 하면, GroupMemberNotFoundException이 발생한다.")
    void leaveGroup_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long groupId = 1L;

        given(groupMemberRepository.findByUserIdAndGroupId(userId, groupId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupMemberService.leaveGroup(userId, groupId))
                .isInstanceOf(GroupMemberNotFoundException.class);
    }

    @Test
    @DisplayName("그룹장이 나가려고 할 때, 다른 그룹 멤버가 존재하면 GroupOwnerLeaveException이 발생한다.")
    void leaveGroup_WhenGroupOwnerAndExistsAnotherGroupMember_ThrowsException() {
        // given
        long userId = 1L;
        long groupId = 1L;
        GroupMember given = GroupMemberFixture.groupOwner(userId, groupId);

        given(groupMemberRepository.findByUserIdAndGroupId(userId, groupId))
                .willReturn(Optional.of(given));
        given(groupMemberRepository.existsByGroupIdAndUserIdNot(groupId, userId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> groupMemberService.leaveGroup(userId, groupId))
                .isInstanceOf(GroupOwnerLeaveException.class);
    }

    @Test
    @DisplayName("그룹 참여자가 아니라면, GroupMemberNotFoundException이 발생한다.")
    void validateParticipant_WhenNotExists_ThrowsException() {
        // given
        long userId = 1L;
        long groupId = 1L;

        given(groupMemberRepository.existsByUserIdAndGroupId(userId, groupId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> groupMemberService.validateParticipant(userId, groupId))
                .isInstanceOf(GroupMemberNotFoundException.class);
    }

    @Test
    @DisplayName("이미 참여 중인 사용자 목록을 빈 리스트로 조회하면, 빈 리스트가 반환된다.")
    void getAlreadyJoinedUserIds_WhenEmptyList_ReturnsEmptyList() {
        // given
        long groupId = 1L;
        List<Long> userIds = List.of();

        // when
        List<Long> found = groupMemberService.getAlreadyJoinedUserIds(groupId, userIds);

        // then
        assertThat(found)
                .isEmpty();
    }

    @Test
    @DisplayName("이미 참여 중인 사용자 목록을 조회하면, 해당 그룹에 속한 사용자 ID 목록이 반환된다.")
    void getAlreadyJoinedUserIds_WhenCalled_ReturnsJoinedUserIds() {
        // given
        long groupId = 1L;
        List<Long> userIds = List.of(1L, 2L, 3L);
        List<Long> joinedUserIds = List.of(1L, 2L);

        given(groupMemberRepository.findAllUserIdByGroupIdAndUserIdIn(groupId, userIds))
                .willReturn(joinedUserIds);

        // when
        List<Long> found = groupMemberService.getAlreadyJoinedUserIds(groupId, userIds);

        // then
        assertThat(found)
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(joinedUserIds);
    }
}
