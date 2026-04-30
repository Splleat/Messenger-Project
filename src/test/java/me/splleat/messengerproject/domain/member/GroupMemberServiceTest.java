package me.splleat.messengerproject.domain.member;

import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.member.exception.GroupMemberAlreadyExistsException;
import me.splleat.messengerproject.domain.member.exception.GroupMemberNotFoundException;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupMemberRepository;
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
import static org.mockito.Mockito.mock;

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

        User user = mock(User.class);
        Group group = mock(Group.class);
        GroupMember groupMember = GroupMember.create(user, group, "test", GroupRole.MEMBER);

        given(groupMember.getUserId())
                .willReturn(userId);
        given(groupMember.getGroupId())
                .willReturn(groupId);

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
    void getAllGroupMemberUserId_WhenEmpty_ReturnsEmptyList() {
        // when
        List<Long> userIdList = groupMemberService.getAllGroupMemberUserId(1L);

        // then
        assertThat(userIdList)
                .isNotNull()
                .isEmpty();
    }
}