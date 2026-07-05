package me.splleat.messengerproject.infrastructure.message.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisNotificationPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic notificationTopic;

    public void publish(String event) {
        redisTemplate.convertAndSend(notificationTopic.getTopic(), event);
    }
}
