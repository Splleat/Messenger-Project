package me.splleat.messengerproject.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("redis.prefix")
public record RedisKeyPrefixProperties(
        String blacklist,
        String refresh
) {}
