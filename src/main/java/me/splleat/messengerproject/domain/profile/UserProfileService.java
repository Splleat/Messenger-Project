package me.splleat.messengerproject.domain.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.profile.exception.UserProfileAlreadyExistsException;
import me.splleat.messengerproject.domain.profile.exception.UserProfileNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserProfileRepository;
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
            throw new UserProfileAlreadyExistsException();
        }

        return userProfileRepository.save(userProfile);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userProfile", key = "#id")
    public UserProfile getUserProfile(Long id) {
        return userProfileRepository.findByUserId(id)
                .orElseThrow(UserProfileNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<UserProfile> getUserProfiles(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userProfileRepository.findAllByUserIdIn(userIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, UserProfile> getUserProfileMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return userProfileRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, profile -> profile));
    }
}
