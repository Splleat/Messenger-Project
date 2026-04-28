package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.member.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("""
        SELECT COUNT(gm) > 1
        FROM GroupMember gm
        WHERE gm.user.id = :userId AND gm.group.id = :groupId
    """)
    boolean existsByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("""
        SELECT gm
        FROM GroupMember gm
        WHERE gm.user.id = :userId AND gm.group.id = :groupId
    """)
    Optional<GroupMember> findByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("""
        SELECT gm.user.id
        FROM GroupMember gm
        WHERE gm.group.id = :groupId
    """)
    List<Long> findAllUserIdByGroupId(@Param("groupId") Long groupId);

    @Query("""
        SELECT gm.nickname
        FROM GroupMember gm
        WHERE gm.user.id = :userId AND gm.group.id = :groupId
    """)
    Optional<String> findNicknameByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
