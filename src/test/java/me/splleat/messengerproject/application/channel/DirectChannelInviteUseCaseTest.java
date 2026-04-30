package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.application.channel.dto.DirectChannelInviteCommand;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DirectChannelInviteUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @InjectMocks
    private DirectChannelInviteUseCase directChannelInviteUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 대상 유저들을 채널에 초대하고 설정을 등록한다.")
    void execute_WhenValidCommand_InvitesUsers() {
        // given
        long userId = 1L;
        long channelId = 1L;
        List<Long> targetIds = List.of(2L, 3L, 4L);
        Channel channel = mock(Channel.class);
        DirectChannelInviteCommand command = new DirectChannelInviteCommand(userId, channelId, targetIds);

        given(channelService.getChannel(channelId))
                .willReturn(channel);
        given(channelUserSettingService.alreadyJoinedIds(command.channelId()))
                .willReturn(Collections.emptyList());

        // when
        assertDoesNotThrow(() -> directChannelInviteUseCase.execute(command));

        // then
        then(channelUserSettingService)
                .should()
                .validateParticipant(command.userId(), command.channelId());

        then(channelService)
                .should()
                .getChannel(channelId);

        then(channelUserSettingService)
                .should()
                .alreadyJoinedIds(channelId);

        then(channelUserSettingService)
                .should()
                .registerAll(anyList());
    }
}