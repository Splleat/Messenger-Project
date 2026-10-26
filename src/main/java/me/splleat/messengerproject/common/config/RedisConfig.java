package me.splleat.messengerproject.common.config;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.property.TopicProperties;
import me.splleat.messengerproject.infrastructure.message.subscriber.RedisMessageSubscriber;
import me.splleat.messengerproject.infrastructure.message.subscriber.RedisNotificationSubscriber;
import me.splleat.messengerproject.infrastructure.message.subscriber.RedisTypingSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
    private final TopicProperties topicProperties;

    @Bean("messageTopic")
    ChannelTopic messageTopic() {
        return new ChannelTopic(topicProperties.message());
    }

    @Bean("typingTopic")
    ChannelTopic typingChannelTopic() {
        return new ChannelTopic(topicProperties.typing());
    }

    @Bean("notificationTopic")
    ChannelTopic notificationTopic() {
        return new ChannelTopic(topicProperties.notification());
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber messageSubscriber,
            RedisTypingSubscriber typingSubscriber,
            RedisNotificationSubscriber notificationSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageSubscriber, messageTopic());
        container.addMessageListener(typingSubscriber, typingChannelTopic());
        container.addMessageListener(notificationSubscriber, notificationTopic());

        return container;
    }
}

