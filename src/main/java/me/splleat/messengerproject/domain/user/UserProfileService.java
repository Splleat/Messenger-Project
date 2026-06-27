package me.splleat.messengerproject.domain.user;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserProfileRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public UserProfile register(UserProfile userProfile) {
        if (userProfileRepository.existsByUserId(userProfile.getUserId())) {
            throw new BusinessException(ErrorCode.USER_PROFILE_ALREADY_EXISTS);
        }

        return userProfileRepository.save(userProfile);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userProfile", key = "#id")
    public UserProfile getUserProfile(Long id) {
        return userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROFILE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Map<Long, UserProfile> getUserProfileMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return userProfileRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, profile -> profile));
    }

    @Transactional
    @CacheEvict(value = "userProfile", key = "#userId")
    public String updateImageUrl(long userId, String imageUrl) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROFILE_NOT_FOUND));

        String oldImageKey = userProfile.getImageUrl();

        userProfile.updateImageUrl(imageUrl);

        return oldImageKey;
    }

    @Transactional
    @CacheEvict(value = "userProfile", key = "#userId")
    public void updateProfile(long userId, String name, String statusMessage) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PROFILE_NOT_FOUND));

        userProfile.updateProfile(name, statusMessage);
    }
}
