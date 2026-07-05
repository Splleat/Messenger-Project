package me.splleat.messengerproject.application.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.infrastructure.cache.SpaceCacheEvictor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SpaceLeaveUseCase {
    private final SpaceMemberService spaceMemberService;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final SpaceCacheEvictor cacheEvictor;

    @Transactional
    public void execute(long userId, long spaceId) {
        spaceMemberService.leaveSpace(userId, spaceId);

        cacheEvictor.evictSpaceList(userId);

        List<Long> spaceChannelIds = channelService.getSpaceChannelIds(spaceId);

        channelUserSettingService.leaveChannels(userId, spaceChannelIds);
    }
}
