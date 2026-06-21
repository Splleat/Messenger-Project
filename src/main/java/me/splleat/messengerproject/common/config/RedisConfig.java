package me.splleat.messengerproject.common.config;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.message.subscriber.RedisSubscriber;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    private final DataRedisProperties redisProperties;
    private static final String TOPIC = "messenger:channel";

    @Bean
    ChannelTopic channelTopic() {
        return new ChannelTopic(TOPIC);
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());

        return new LettuceConnectionFactory(config);
    }

    @Bean
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory, RedisSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, channelTopic());

        return container;
    }
}

