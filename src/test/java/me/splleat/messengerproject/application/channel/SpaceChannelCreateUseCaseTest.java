package me.splleat.messengerproject.application.channel;

import me.splleat.messengerproject.application.channel.dto.SpaceChannelCreateCommand;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.space.SpaceRole;
import me.splleat.messengerproject.infrastructure.cache.ChannelCacheEvictor;
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
class SpaceChannelCreateUseCaseTest {

    @Mock
    private SpaceMemberService spaceMemberService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @Mock
    private ChannelCacheEvictor cacheEvictor;

    @InjectMocks
    private SpaceChannelCreateUseCase spaceChannelCreateUseCase;

    @Test
    @DisplayName("올바른 명령이 주어지면 그룹 채널을 생성하고 모든 그룹 멤버의 유저 설정을 등록한다.")
    void execute_WhenValidCommand_CreatesSpaceChannel() {
        // given
        long userId = 1L;
        long spaceId = 1L;
        SpaceChannelCreateCommand command = new SpaceChannelCreateCommand(userId, spaceId, "testChannel", ChannelType.TEXT);
        SpaceMember spaceMember = mock(SpaceMember.class);
        Channel channel = mock(Channel.class);
        List<Long> spaceMemberIds = List.of(1L, 2L, 3L);

        given(spaceMemberService.getSpaceMember(userId, spaceId))
                .willReturn(spaceMember);
        given(channelService.register(any(Channel.class)))
                .willReturn(channel);
        given(spaceMemberService.getAllParticipantUserIds(spaceId))
                .willReturn(spaceMemberIds);

        // when
        assertDoesNotThrow(() -> spaceChannelCreateUseCase.execute(command));

        // then
        then(spaceMemberService)
                .should()
                .getSpaceMember(userId, spaceId);

        then(spaceMember)
                .should()
                .validatePermission(SpaceRole.ADMIN);

        then(channelService)
                .should()
                .register(any(Channel.class));

        then(spaceMemberService)
                .should()
                .getAllParticipantUserIds(spaceId);

        then(channelUserSettingService)
                .should()
                .registerAll(anyList());
    }

}