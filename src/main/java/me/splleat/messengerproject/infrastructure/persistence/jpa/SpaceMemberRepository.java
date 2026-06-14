package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.space.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
    boolean existsByUserIdAndSpaceId(Long userId, Long spaceId);

    Optional<SpaceMember> findByUserIdAndSpaceId(Long userId, Long spaceId);

    @Query("""
        SELECT gm.userId
        FROM SpaceMember gm
        WHERE gm.spaceId = :spaceId
    """)
    List<Long> findAllUserIdBySpaceId(@Param("spaceId") Long spaceId);

    @Query("""
        SELECT gm.nickname
        FROM SpaceMember gm
        WHERE gm.userId = :userId AND gm.spaceId = :spaceId
    """)
    Optional<String> findNicknameByUserIdAndSpaceId(@Param("userId") Long userId, @Param("spaceId") Long spaceId);

    @Query("""
        SELECT gm.spaceId
        FROM SpaceMember gm
        WHERE gm.userId = :userId
    """)
    List<Long> findAllSpaceIdByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT gm.userId
        FROM SpaceMember gm
        WHERE gm.spaceId = :spaceId AND gm.userId IN :userIds
    """)
    List<Long> findAllUserIdBySpaceIdAndUserIdIn(@Param("spaceId") Long spaceId, @Param("userIds") List<Long> userIds);

    void deleteByUserIdAndSpaceId(Long userId, Long spaceId);

    boolean existsBySpaceIdAndUserIdNot(Long spaceId, Long userId);
}
