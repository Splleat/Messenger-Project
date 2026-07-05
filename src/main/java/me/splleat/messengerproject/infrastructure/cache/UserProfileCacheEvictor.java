package me.splleat.messengerproject.infrastructure.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class UserProfileCacheEvictor {

    @CacheEvict(value = "myProfile", key = "#userId")
    public void evictMyProfile(long userId) {}

    @CacheEvict(value = "userProfile", key = "#userId")
    public void evictUserProfile(long userId) {}
}
