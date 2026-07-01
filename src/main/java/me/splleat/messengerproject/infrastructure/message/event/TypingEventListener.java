package me.splleat.messengerproject.infrastructure.message.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.splleat.messengerproject.infrastructure.message.publisher.RedisTypingPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TypingEventListener {
    private final RedisTypingPublisher typingPublisher;
    private final JsonMapper jsonMapper;

    @EventListener
    public void handleTypingEvent(TypingEvent event) {
        try {
            typingPublisher.publish(jsonMapper.writeValueAsString(event));
        } catch (Exception e) {
            log.error("메시지 타이핑 이벤트 발행 실패: {}", e.getMessage());
        }
    }
}
