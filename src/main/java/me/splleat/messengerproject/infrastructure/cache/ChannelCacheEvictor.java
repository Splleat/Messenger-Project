package me.splleat.messengerproject.infrastructure.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class ChannelCacheEvictor {

    @CacheEvict(value = "directChannelList", key = "#userId")
    public void evictDirectChannels(long userId) {}

    @CacheEvict(value = "spaceChannelList", key = "#userId + '-' + #spaceId")
    public void evictSpaceChannels(long userId, long spaceId) {}
}
