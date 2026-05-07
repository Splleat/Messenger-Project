package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.member.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);

    List<Long> findAllUserIdByGroupId(Long groupId);

    @Query("""
        SELECT gm.nickname
        FROM GroupMember gm
        WHERE gm.userId = :userId AND gm.groupId = :groupId
    """)
    Optional<String> findNicknameByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);
}
