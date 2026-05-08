package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ChannelListGetUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional(readOnly = true)
    public List<ChannelListResult> execute(long userId) {
        List<Long> joinedChannelIds = channelUserSettingService.getJoinedChannelIds(userId);

        List<Channel> joinedChannels = channelService.getAllChannelByChannelIds(joinedChannelIds);

        return joinedChannels.stream()
                .map(ChannelListResult::from)
                .toList();
    }
}
