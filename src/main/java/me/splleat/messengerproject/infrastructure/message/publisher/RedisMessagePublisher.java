package me.splleat.messengerproject.infrastructure.message.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisMessagePublisher implements MessagePublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    public RedisMessagePublisher(
            StringRedisTemplate redisTemplate,
            @Qualifier("messageTopic") ChannelTopic channelTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
