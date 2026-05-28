package me.splleat.messengerproject.application.group;

import me.splleat.messengerproject.application.group.dto.GroupCreateCommand;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GroupCreateUseCaseTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupMemberService groupMemberService;

    @InjectMocks
    private GroupCreateUseCase groupCreateUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 그룹을 생성하고 생성한 유저를 그룹장으로 등록한다.")
    void execute_WhenValidCommand_CreatesGroup() {
        // given
        long userId = 1L;
        GroupCreateCommand command = new GroupCreateCommand(userId, "testName");
        UserProfile profile = mock(UserProfile.class);
        Group group = mock(Group.class);

        given(userProfileService.getUserProfile(userId))
                .willReturn(profile);
        given(groupService.register(any(Group.class)))
                .willReturn(group);

        // when
        assertDoesNotThrow(() -> groupCreateUseCase.execute(command));

        // then

        then(groupService)
                .should()
                .register(any(Group.class));

        then(groupMemberService)
                .should()
                .register(any(GroupMember.class));
    }
}