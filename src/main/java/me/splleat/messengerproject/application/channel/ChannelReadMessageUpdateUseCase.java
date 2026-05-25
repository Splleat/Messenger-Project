package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class ChannelReadMessageUpdateUseCase {
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(long userId, long channelId, long lastReadMessageId) {
        ChannelUserSetting setting = channelUserSettingService.getChannelUserSetting(userId, channelId);

        setting.updateLastReadMessage(lastReadMessageId);
    }
}
