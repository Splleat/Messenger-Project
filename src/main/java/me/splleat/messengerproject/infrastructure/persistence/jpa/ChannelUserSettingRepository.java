package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ChannelUserSettingRepository extends JpaRepository<ChannelUserSetting, Long> {

    boolean existsByUserIdAndChannelId(Long userId, Long channelId);

    void deleteAllByUserIdAndChannelIdIn(Long userId, Collection<Long> channelIds);

    @Query("""
        SELECT cus.userId
        FROM ChannelUserSetting cus
        WHERE cus.channelId = :channelId AND cus.userId IN :userIds
    """)
    List<Long> findAllUserIdByChannelIdAndUserIdIn(@Param("channelId") Long channelId, @Param("userIds") Collection<Long> userIds);

    List<ChannelUserSetting> findAllByUserIdAndChannelIdIn(Long userId, Collection<Long> channelIds);

    List<ChannelUserSetting> findAllByChannelIdAndUserIdIn(Long channelId, Collection<Long> userIds);

    @Query("""
        SELECT cus.channelId
        FROM ChannelUserSetting cus
        WHERE cus.userId = :userId
    """)
    List<Long> findAllChannelIdByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndChannelId(Long userId, Long channelId);
}
