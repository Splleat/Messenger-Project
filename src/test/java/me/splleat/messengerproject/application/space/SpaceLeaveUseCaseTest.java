package me.splleat.messengerproject.application.space;

import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.infrastructure.cache.SpaceCacheEvictor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SpaceLeaveUseCaseTest {

    @Mock
    private SpaceMemberService spaceMemberService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelUserSettingService channelUserSettingService;

    @Mock
    private SpaceCacheEvictor cacheEvictor;

    @InjectMocks
    private SpaceLeaveUseCase spaceLeaveUseCase;

    @Test
    @DisplayName("스페이스 나가기 요청 시, 스페이스 및 관련 채널들에서 탈퇴하고 캐시를 삭제한다.")
    void execute_LeavesSpaceAndChannelsAndEvictsCache() {
        // given
        long userId = 1L;
        long spaceId = 1L;
        List<Long> channelIds = List.of(10L, 11L);

        given(channelService.getSpaceChannelIds(spaceId))
                .willReturn(channelIds);

        // when
        spaceLeaveUseCase.execute(userId, spaceId);

        // then
        then(spaceMemberService)
                .should()
                .leaveSpace(userId, spaceId);

        then(cacheEvictor)
                .should()
                .evictSpaceList(userId);

        then(channelService)
                .should()
                .getSpaceChannelIds(spaceId);

        then(channelUserSettingService)
                .should()
                .leaveChannels(userId, channelIds);
    }
}
