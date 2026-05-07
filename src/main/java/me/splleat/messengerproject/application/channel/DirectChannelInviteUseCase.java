package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.DirectChannelInviteCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UseCase
@RequiredArgsConstructor
public class DirectChannelInviteUseCase {
    private final ChannelUserSettingService channelUserSettingService;

    @Transactional
    public void execute(DirectChannelInviteCommand command) {
        
        channelUserSettingService.validateParticipant(command.userId(), command.channelId());

        // 이미 채널에 참여 중인 사용자 아이디 목록
        Set<Long> alreadyJoinedUserIds = new HashSet<>(channelUserSettingService.alreadyJoinedIds(command.channelId(), command.targetIds()));

        // 새롭게 채널에 참여하는 사용자의 설정 정보 생성
        List<ChannelUserSetting> newChannelSettings = command.targetIds().stream()
                .filter(targetId -> !alreadyJoinedUserIds.contains(targetId))
                .map(targetId -> ChannelUserSetting.create(targetId, command.channelId()))
                .toList();

        channelUserSettingService.registerAll(newChannelSettings);
    }
}
