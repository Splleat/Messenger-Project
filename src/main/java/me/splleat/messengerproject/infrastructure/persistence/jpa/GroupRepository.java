package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByIdIn(Collection<Long> ids);
}
