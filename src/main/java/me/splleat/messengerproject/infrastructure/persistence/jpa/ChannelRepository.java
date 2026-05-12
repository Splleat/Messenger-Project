package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Query("""
        SELECT c.id
        FROM Channel c
        WHERE c.groupId = :groupId
    """)
    List<Long> findAllChannelIdByGroupId(@Param("groupId") Long groupId);

    List<Channel> findAllByGroupIdIsNullAndIdIn(List<Long> ids);

    List<Channel> findAllByGroupId(Long groupId);

    boolean existsByIdAndGroupId(Long id, Long groupId);
}
