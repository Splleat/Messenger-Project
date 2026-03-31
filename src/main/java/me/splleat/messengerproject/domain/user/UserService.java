package me.splleat.messengerproject.domain.user;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.user.exception.UserNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
}
