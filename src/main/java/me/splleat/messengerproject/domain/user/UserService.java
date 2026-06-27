package me.splleat.messengerproject.domain.user;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException(ErrorCode.USER_EMAIL_DUPLICATED);
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getActiveUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }

        return user;
    }

    @Transactional(readOnly = true)
    public void validateExistsAll(List<Long> ids) {
        Set<Long> distinctIds = new HashSet<>(ids);

        int count = userRepository.countAllByIdIn(distinctIds);

        if (count != distinctIds.size()) {
            throw new BusinessException(ErrorCode.TARGET_USER_NOT_FOUND);
        }
    }
}