package me.splleat.messengerproject.infrastructure.message.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.message.event.TypingEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTypingSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final JsonMapper jsonMapper;

    @Override
    public void onMessage(@NonNull Message message, byte @Nullable [] pattern) {
        try {
            TypingEvent event = jsonMapper.readValue(message.getBody(), TypingEvent.class);

            String destination = "/sub/channels/" + event.channelId() + "/typing";

            messagingTemplate.convertAndSend(destination, event);
        } catch (Exception e) {
            log.error("이벤트 라우팅 실패: {}", e.getMessage());
        }
    }
}
