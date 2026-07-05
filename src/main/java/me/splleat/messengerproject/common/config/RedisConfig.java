package me.splleat.messengerproject.common.config;

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
public class RedisConfig {

    private static final String TOPIC = "messenger:message";
    private static final String TYPING_TOPIC = "messenger:typing";
    private static final String NOTIFICATION_TOPIC = "messenger:notification";

    @Bean("messageTopic")
    ChannelTopic messageTopic() {
        return new ChannelTopic(TOPIC);
    }

    @Bean("typingTopic")
    ChannelTopic typingChannelTopic() {
        return new ChannelTopic(TYPING_TOPIC);
    }

    @Bean("notificationTopic")
    ChannelTopic notificationTopic() {
        return new ChannelTopic(NOTIFICATION_TOPIC);
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

