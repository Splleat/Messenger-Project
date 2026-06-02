package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.member.GroupMemberService;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageService;
import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.infrastructure.message.outbox.DomainCreatedEvent;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class SendMessageUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final GroupMemberService groupMemberService;
    private final UserProfileService userProfileService;
    private final MessageService messageService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void execute(MessageCreateCommand command) {
        // 채널 및 채널 설정 정보 확인
        ChannelUserSetting setting = channelUserSettingService.getChannelUserSetting(command.senderId(), command.channelId());
        Channel channel = channelService.getChannel(command.channelId());

        // 메시지 생성
        Message message = command.toEntity();
        Message created = messageService.registerWithIdempotency(message);

        // 마지막으로 읽은 메시지 업데이트
        setting.updateLastReadMessage(created.getId());

        // 응답으로 내려줄 프로필 정보 조회
        UserProfile userProfile = userProfileService.getUserProfile(command.senderId());
        String username = userProfile.getName();
        String profileUrl = userProfile.getImageUrl();

        if (channel.isGroupChannel()) {
            username = groupMemberService.getNickname(command.senderId(), channel.getGroupId())
                    .orElse(username);
        }

        publisher.publishEvent(DomainCreatedEvent.of(created, MessageResponse.of(username, profileUrl, created)));
    }
}
