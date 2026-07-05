package me.splleat.messengerproject.application.notification;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelService;
import me.splleat.messengerproject.infrastructure.message.publisher.RedisNotificationPublisher;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.ChannelQueryRepository;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.NotificationRecipientResult;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class NotificationNewMessageUseCase {
    private final ChannelService channelService;
    private final ChannelQueryRepository channelQueryRepository;
    private final RedisNotificationPublisher publisher;
    private final JsonMapper jsonMapper;

    @Transactional(readOnly = true)
    public void execute(MessageResponse message) {
        Channel channel = channelService.getChannel(message.channelId());

        List<NotificationRecipientResult> recipients = channelQueryRepository.findNotificationRecipients(message.channelId(), message.userId());

        recipients.stream()
                .filter(r -> !r.isMuted())
                .map(r -> r.toEvent(channel.getSpaceId(), message))
                .forEach(event -> publisher.publish(jsonMapper.writeValueAsString(event)));
    }
}
