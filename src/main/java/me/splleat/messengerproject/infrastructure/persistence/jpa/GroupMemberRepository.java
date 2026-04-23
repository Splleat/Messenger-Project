package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.member.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);

    List<Long> findAllUserIdByGroupId(Long groupId);

    Optional<String> findNicknameByUserIdAndGroupId(Long userId, Long groupId);
}
