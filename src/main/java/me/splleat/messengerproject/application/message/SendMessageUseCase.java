package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.message.*;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageCreateEvent;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SendMessageUseCase {
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final SpaceMemberService spaceMemberService;
    private final UserProfileService userProfileService;
    private final MessageService messageService;
    private final ApplicationEventPublisher eventPublisher;
    private final AttachmentService attachmentService;

    @Transactional
    public MessageResponse execute(MessageCreateCommand command) {
        // 채널 및 채널 설정 정보 확인
        ChannelUserSetting setting = channelUserSettingService.getChannelUserSetting(command.senderId(), command.channelId());
        Channel channel = channelService.getChannel(command.channelId());

        // 메시지 생성
        Message message = command.toMessage();
        MessageRegistration result = messageService.registerWithIdempotency(message);

        long messageId = result.message().getId();

        // 마지막으로 읽은 메시지 업데이트
        setting.updateLastReadMessage(messageId);

        // 응답으로 내려줄 프로필 정보 조회
        UserProfile userProfile = userProfileService.getUserProfile(command.senderId());
        String username = userProfile.getName();
        String profileUrl = userProfile.getImageUrl();

        if (channel.isSpaceChannel()) {
            username = spaceMemberService.getNickname(command.senderId(), channel.getSpaceId())
                    .orElse(username);
        }

        List<Attachment> attachments;

        // 메시지가 새로 생성된 경우
        if (result.isCreated()) {
            boolean hasAttachments = (command.attachments() != null && !command.attachments().isEmpty());

            if (hasAttachments) {
                attachments = command.attachments().stream()
                        .map(req -> req.toEntity(messageId))
                        .toList();
                attachmentService.registerAll(attachments);
            } else {
                attachments = List.of();
            }

            MessageResponse response = MessageResponse.of(username, profileUrl, result.message(), attachments);

            // 이벤트 발행
            eventPublisher.publishEvent(MessageCreateEvent.from(response));

            return response;
        }

        // 이미 생성된 메시지인 경우 기존 첨부파일 조회
        attachments = attachmentService.getAttachments(messageId);

        return MessageResponse.of(username, profileUrl, result.message(), attachments);
    }
}
