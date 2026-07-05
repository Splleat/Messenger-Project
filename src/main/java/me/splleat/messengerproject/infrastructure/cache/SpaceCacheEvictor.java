package me.splleat.messengerproject.infrastructure.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class SpaceCacheEvictor {

    @CacheEvict(value = "space", key = "#spaceId")
    public void evictSpace(long spaceId) {}

    @CacheEvict(value = "spaceList", key = "#userId")
    public void evictSpaceList(long userId) {}
}
