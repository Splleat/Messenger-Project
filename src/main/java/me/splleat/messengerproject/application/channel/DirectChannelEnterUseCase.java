package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.DirectChannelEnterCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelEnterResponse;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class DirectChannelEnterUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional(readOnly = true)
    public ChannelEnterResponse execute(DirectChannelEnterCommand command) {
        ChannelUserSetting channelUserSetting = channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId());

        Long lastReadMessageId = channelUserSetting.getLastReadMessageId();

        if (lastReadMessageId == null) {
            return messageQueryRepository.findByNewest(command.channelId());
        }

        return messageQueryRepository.findByAroundId(command.channelId(), lastReadMessageId);
    }
}
