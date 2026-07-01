package me.splleat.messengerproject.application.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.domain.message.*;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.message.event.MessageCreatedEvent;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import me.splleat.messengerproject.interfaces.websocket.message.dto.AttachmentResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SendMessageUseCase {
    private final StorageUrlMapper storageUrlMapper;
    private final ChannelService channelService;
    private final ChannelUserSettingService channelUserSettingService;
    private final SpaceMemberService spaceMemberService;
    private final UserProfileService userProfileService;
    private final MessageService messageService;
    private final AttachmentService attachmentService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MessageResponse execute(MessageCreateCommand command) {
        // 채널 및 채널 설정 정보 확인
        ChannelUserSetting setting = channelUserSettingService.getChannelUserSetting(command.userId(), command.channelId());
        Channel channel = channelService.getChannel(command.channelId());

        // 메시지 생성
        Message message = command.toMessage();
        MessageRegistration result = messageService.registerWithIdempotency(message);
        long messageId = result.message().getId();

        // 마지막으로 읽은 메시지 업데이트
        setting.updateLastReadMessage(messageId);

        // 응답으로 내려줄 프로필 정보 조회
        UserProfile userProfile = userProfileService.getUserProfile(command.userId());
        String username = resolveUsername(command.userId(), channel, userProfile);
        String profileUrl = storageUrlMapper.resolve(userProfile.getImageUrl());

        // 첨부파일 등록
        boolean hasAttachments = (command.attachments() != null && !command.attachments().isEmpty());

        List<Attachment> attachments = (hasAttachments) ? resolveAttachments(command, messageId, result.isCreated()) : List.of();

        // 메시지 응답 객체 생성
        List<AttachmentResponse> attachmentResponses = attachments.stream()
                .map(att -> AttachmentResponse.of(att, storageUrlMapper.resolve(att.getUrl())))
                .toList();

        MessageResponse response = MessageResponse.of(username, profileUrl, result.message(), attachmentResponses);

        // 새로운 메시지라면 이벤트 발행
        if (result.isCreated()) {
            eventPublisher.publishEvent(MessageEvent.from(MessageCreatedEvent.from(response)));
        }

        return response;
    }

    private String resolveUsername(long senderId, Channel channel, UserProfile userProfile) {
        if (channel.isSpaceChannel()) {
            return spaceMemberService.getNickname(senderId, channel.getSpaceId())
                    .orElse(userProfile.getName());
        }

        return userProfile.getName();
    }

    private List<Attachment> resolveAttachments(MessageCreateCommand command, long messageId, boolean isCreated) {
        if (!isCreated) {
            return attachmentService.getAttachments(messageId);
        }

        List<Attachment> attachments = command.attachments().stream()
                .map(req -> req.toEntity(messageId))
                .toList();

        attachmentService.registerAll(attachments);

        return attachments;
    }
}
