package me.splleat.messengerproject.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("redis.topic")
public record TopicProperties(
        String message,
        String typing,
        String notification
) {}
