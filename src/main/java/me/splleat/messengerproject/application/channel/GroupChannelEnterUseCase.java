package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.GroupChannelEnterCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelEnterResponse;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class GroupChannelEnterUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final GroupMemberService groupMemberService;

    @Transactional(readOnly = true)
    public ChannelEnterResponse execute(GroupChannelEnterCommand command) {
        // 요청자가 그룹 멤버인지 확인
        groupMemberService.validateParticipant(command.userId(), command.groupId());

        // 해당 채널이 그룹에 속해 있는지 확인
        channelService.validateInGroup(command.groupId(), command.channelId());

        // 해당 채널에 대한 설정이 존재하지 않으면 생성
        ChannelUserSetting channelUserSetting = channelUserSettingService.registerIfAbsent(command.userId(), command.channelId());

        Long lastReadMessageId = channelUserSetting.getLastReadMessageId();

        // 채널 처음 입장 시 최근 20개 메시지 반환
        if (lastReadMessageId == null) {
            return messageQueryRepository.findByNewest(command.channelId());
        }

        // 마지막으로 읽은 메시지 기준으로 앞뒤로 20개씩 반환
        return messageQueryRepository.findByAroundId(command.channelId(), lastReadMessageId);
    }
}
