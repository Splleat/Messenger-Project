package me.splleat.messengerproject.infrastructure.message.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.message.event.NotificationEvent;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.NotificationRecipientResult;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisNotificationSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final JsonMapper jsonMapper;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        try {
            NotificationEvent response = jsonMapper.readValue(message.getBody(), NotificationEvent.class);

            String destination = "/sub/users/" + response.recipientUserId() + "/notifications";

            messagingTemplate.convertAndSend(destination, response);
        } catch (Exception e) {
            log.error("메시지 알림 라우팅 실패: {}", e.getMessage());
        }
    }
}
