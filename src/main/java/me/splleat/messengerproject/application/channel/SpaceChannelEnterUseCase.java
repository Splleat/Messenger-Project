package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.MessageQueryRepository;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class SpaceChannelEnterUseCase {
    private final MessageQueryRepository messageQueryRepository;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final SpaceMemberService spaceMemberService;

    @Transactional
    public ChannelEnterResult execute(long userId, long spaceId, long channelId) {
        // 요청자가 그룹 멤버인지 확인
        spaceMemberService.validateParticipant(userId, spaceId);

        // 해당 채널이 그룹에 속해 있는지 확인
        channelService.validateInSpace(spaceId, channelId);

        // 해당 채널에 대한 설정이 존재하지 않으면 생성
        ChannelUserSetting channelUserSetting = channelUserSettingService.registerIfAbsent(userId, channelId);

        Long lastReadMessageId = channelUserSetting.getLastReadMessageId();

        // 메시지 응답 생성
        ChannelEnterResult response = (lastReadMessageId == null) ?
                messageQueryRepository.findByNewest(channelId) :
                messageQueryRepository.findByAroundId(channelId, lastReadMessageId);

        // 마지막으로 읽은 메시지 업데이트
        channelUserSetting.updateLastReadMessage(response.nextCursorId());

        return response;
    }
}
