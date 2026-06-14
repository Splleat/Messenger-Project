package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findAllByIdIn(Collection<Long> ids);
}
