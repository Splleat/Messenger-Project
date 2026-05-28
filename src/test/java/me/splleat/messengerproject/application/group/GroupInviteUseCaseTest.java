package me.splleat.messengerproject.application.group;

import me.splleat.messengerproject.application.group.dto.GroupInviteCommand;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.support.fixture.UserProfileFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GroupInviteUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private GroupMemberService groupMemberService;

    @InjectMocks
    private GroupInviteUseCase groupInviteUseCase;

    @Test
    @DisplayName("그룹 초대 시, 이미 가입된 사용자를 제외하고 새로운 멤버를 등록한다.")
    void execute_WhenCalled_InvitesNewMembers() {
        // given
        long inviterId = 1L;
        long groupId = 10L;
        List<Long> targetIds = List.of(2L, 3L, 4L);
        GroupInviteCommand command = new GroupInviteCommand(inviterId, groupId, targetIds);

        List<Long> alreadyJoinedIds = List.of(2L);
        UserProfile profile = UserProfileFixture.defaultUserProfile(3L);

        given(groupMemberService.getAlreadyJoinedUserIds(groupId, targetIds))
                .willReturn(alreadyJoinedIds);
        
        given(userProfileService.getUserProfileMap(List.of(3L, 4L)))
                .willReturn(Map.of(3L, profile));

        // when
        groupInviteUseCase.execute(command);

        // then
        then(groupMemberService)
                .should()
                .validateParticipant(inviterId, groupId);

        then(userService)
                .should()
                .validateExistsAll(targetIds);

        then(groupMemberService)
                .should()
                .registerAll(anyList());
        
        then(groupMemberService)
                .should()
                .registerAll(argThat(members -> members.size() == 2));
    }
}
