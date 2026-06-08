package me.splleat.messengerproject.infrastructure.message.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final JsonMapper jsonMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        try {
            MessageResponse response = jsonMapper.readValue(message.toString(), MessageResponse.class);

            String destination = "/sub/channels/" + response.channelId() + "/messages";

            messagingTemplate.convertAndSend(destination, response);
        } catch (Exception e) {
            log.error("메시지 라우팅 실패: {}", e.getMessage());
        }
    }
}
