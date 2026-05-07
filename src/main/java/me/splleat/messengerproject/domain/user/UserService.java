package me.splleat.messengerproject.domain.user;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.user.exception.UserDeactivatedException;
import me.splleat.messengerproject.domain.user.exception.UserEmailDuplicatedException;
import me.splleat.messengerproject.domain.user.exception.UserNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserEmailDuplicatedException();
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public User getActiveUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserDeactivatedException();
        }

        return user;
    }
}
