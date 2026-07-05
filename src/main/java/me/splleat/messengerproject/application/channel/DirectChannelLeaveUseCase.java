package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class DirectChannelLeaveUseCase {
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(long userId, long channelId) {
        channelUserSettingService.leaveChannel(userId, channelId);
    }
}
