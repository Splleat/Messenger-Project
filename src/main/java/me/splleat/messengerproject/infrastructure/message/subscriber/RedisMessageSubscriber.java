package me.splleat.messengerproject.infrastructure.message.subscriber;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final JsonMapper jsonMapper;
    private final MeterRegistry registry;

    @Override
    public void onMessage(@NonNull Message message, byte @Nullable [] pattern) {
        try {
            MessageEvent<?> response = jsonMapper.readValue(message.toString(), MessageEvent.class);

            String destination = "/sub/channels/" + response.channelId() + "/messages";

            messagingTemplate.convertAndSend(destination, response);

            recordMessagePublish(response);
        } catch (Exception e) {
            log.error("메시지 라우팅 실패: {}", e.getMessage());
        }
    }

    private void recordMessagePublish(MessageEvent<?> event) {
        Duration latency = Duration.between(event.createdAt(), LocalDateTime.now());
        registry.timer("message.publish.latency").record(latency);
    }
}
