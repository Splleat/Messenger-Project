package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class DirectChannelEnterUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public ChannelEnterResult execute(long userId, long channelId) {
        ChannelUserSetting channelUserSetting = channelUserSettingService.getChannelUserSetting(userId, channelId);

        Long lastReadMessageId = channelUserSetting.getLastReadMessageId();

        if (lastReadMessageId == null) {
            ChannelEnterResult response = messageQueryRepository.findByNewest(channelId);
            channelUserSetting.updateLastReadMessage(response.nextCursorId());
            return response;
        }

        ChannelEnterResult response = messageQueryRepository.findByAroundId(channelId, lastReadMessageId);

        channelUserSetting.updateLastReadMessage(response.nextCursorId());

        return response;
    }
}
