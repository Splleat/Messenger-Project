package me.splleat.messengerproject.infrastructure.message.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisTypingPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic typingTopic;

    public RedisTypingPublisher(
            StringRedisTemplate redisTemplate,
            @Qualifier("typingTopic") ChannelTopic typingTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.typingTopic = typingTopic;
    }

    public void publish(String event) {
        redisTemplate.convertAndSend(typingTopic.getTopic(), event);
    }
}
