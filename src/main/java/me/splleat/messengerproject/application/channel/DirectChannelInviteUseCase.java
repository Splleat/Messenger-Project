package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.DirectChannelInviteCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class DirectChannelInviteUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(DirectChannelInviteCommand command) {
        
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());
        
        Channel channel = channelService.getChannel(command.channelId());

        List<Long> alreadyJoinedIds = channelUserSettingService.alreadyJoinedIds(command.channelId());
        
        List<ChannelUserSetting> targetUserSettingList = command.targetIds().stream()
                .filter(target -> !alreadyJoinedIds.contains(target))
                .map(target -> ChannelUserSetting.create(target, channel))
                .toList();
        
        channelUserSettingService.registerAll(targetUserSettingList);
    }
}
