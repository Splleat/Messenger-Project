package me.splleat.messengerproject.domain.profile;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.profile.exception.UserProfileAlreadyExistsException;
import me.splleat.messengerproject.domain.profile.exception.UserProfileNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public void register(UserProfile userProfile) {
        if (userProfileRepository.existsById(userProfile.getId())) {
            throw new UserProfileAlreadyExistsException();
        }

        userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfile(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(UserProfileNotFoundException::new);
    }
}
