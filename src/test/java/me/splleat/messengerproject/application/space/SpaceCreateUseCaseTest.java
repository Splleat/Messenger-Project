package me.splleat.messengerproject.application.space;

import me.splleat.messengerproject.application.space.dto.SpaceCreateCommand;
import me.splleat.messengerproject.domain.space.Space;
import me.splleat.messengerproject.domain.space.SpaceService;
import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
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
class SpaceCreateUseCaseTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private SpaceService spaceService;

    @Mock
    private SpaceMemberService spaceMemberService;

    @InjectMocks
    private SpaceCreateUseCase spaceCreateUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 그룹을 생성하고 생성한 유저를 그룹장으로 등록한다.")
    void execute_WhenValidCommand_CreatesSpace() {
        // given
        long userId = 1L;
        SpaceCreateCommand command = new SpaceCreateCommand(userId, "testName");
        UserProfile profile = mock(UserProfile.class);
        Space space = mock(Space.class);

        given(userProfileService.getUserProfile(userId))
                .willReturn(profile);
        given(spaceService.register(any(Space.class)))
                .willReturn(space);

        // when
        assertDoesNotThrow(() -> spaceCreateUseCase.execute(command));

        // then

        then(spaceService)
                .should()
                .register(any(Space.class));

        then(spaceMemberService)
                .should()
                .register(any(SpaceMember.class));
    }
}