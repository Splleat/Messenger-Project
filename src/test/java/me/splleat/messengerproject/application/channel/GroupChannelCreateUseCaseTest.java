package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.application.channel.dto.GroupChannelCreateCommand;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.group.GroupMember;
import me.splleat.messengerproject.domain.group.GroupMemberService;
import me.splleat.messengerproject.domain.group.GroupRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GroupChannelCreateUseCaseTest {

    @Mock
    private GroupMemberService groupMemberService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @InjectMocks
    private GroupChannelCreateUseCase groupChannelCreateUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 그룹 채널을 생성하고 모든 그룹 멤버의 유저 설정을 등록한다.")
    void execute_WhenValidCommand_CreatesGroupChannel() {
        // given
        long userId = 1L;
        long groupId = 1L;
        GroupChannelCreateCommand command = new GroupChannelCreateCommand(userId, groupId, "testChannel", ChannelType.TEXT);
        GroupMember groupMember = mock(GroupMember.class);
        Channel channel = mock(Channel.class);
        List<Long> groupMemberIds = List.of(1L, 2L, 3L);

        given(groupMemberService.getGroupMember(userId, groupId))
                .willReturn(groupMember);
        given(channelService.register(any(Channel.class)))
                .willReturn(channel);
        given(groupMemberService.getAllParticipantUserIds(groupId))
                .willReturn(groupMemberIds);

        // when
        assertDoesNotThrow(() -> groupChannelCreateUseCase.execute(command));

        // then
        then(groupMemberService)
                .should()
                .getGroupMember(userId, groupId);

        then(groupMember)
                .should()
                .validatePermission(GroupRole.ADMIN);

        then(channelService)
                .should()
                .register(any(Channel.class));

        then(groupMemberService)
                .should()
                .getAllParticipantUserIds(groupId);

        then(channelUserSettingService)
                .should()
                .registerAll(anyList());
    }

}