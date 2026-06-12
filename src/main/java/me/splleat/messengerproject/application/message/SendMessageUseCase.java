package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageRegistration;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.message.outbox.DomainCreatedEvent;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class SendMessageUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final SpaceMemberService spaceMemberService;
    private final UserProfileService userProfileService;
    private final MessageService messageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MessageResponse execute(MessageCreateCommand command) {
        // 채널 및 채널 설정 정보 확인
        ChannelUserSetting setting = channelUserSettingService.getChannelUserSetting(command.senderId(), command.channelId());
        Channel channel = channelService.getChannel(command.channelId());

        // 메시지 생성
        Message message = command.toEntity();
        MessageRegistration result = messageService.registerWithIdempotency(message);

        // 마지막으로 읽은 메시지 업데이트
        setting.updateLastReadMessage(result.message().getId());

        // 응답으로 내려줄 프로필 정보 조회
        UserProfile userProfile = userProfileService.getUserProfile(command.senderId());
        String username = userProfile.getName();
        String profileUrl = userProfile.getImageUrl();

        if (channel.isSpaceChannel()) {
            username = spaceMemberService.getNickname(command.senderId(), channel.getSpaceId())
                    .orElse(username);
        }

        MessageResponse response = MessageResponse.of(username, profileUrl, result.message());

        // 메시지가 새로 생성된 경우에만 이벤트를 발행
        if (result.isCreated()) {
            eventPublisher.publishEvent(DomainCreatedEvent.of(result.message(), response));
        }

        return response;
    }
}
