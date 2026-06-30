package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.user.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<UserProfile> findAllByUserIdIn(Collection<Long> userIds);

    List<UserProfile> findByNameContainingIgnoreCaseAndUserIdNot(String name, Long userId, Pageable pageable);
}

