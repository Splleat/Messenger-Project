package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByName(String name);

    List<UserProfile> findAllByIdIn(Collection<Long> ids);
}
