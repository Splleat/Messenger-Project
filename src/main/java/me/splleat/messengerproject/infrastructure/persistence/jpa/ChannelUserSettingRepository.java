package me.splleat.messengerproject.infrastructure.persistence.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelUserSettingRepository extends JpaRepository<ChannelUserSetting, Long> {
    @Query("""
        SELECT COUNT(cus)
        FROM ChannelUserSetting cus
        WHERE cus.userId = :userId AND cus.channel.id = :channelId
    """)
    boolean existsByUserIdAndChannelId(@Param("userId") Long userId, @Param("channelId") Long channelId);

    @Query("""
        SELECT cus.userId
        FROM ChannelUserSetting cus
        WHERE cus.channel.id = :channelId
    """)
    List<Long> findAllUserIdByChannelId(@Param("channelId") Long channelId);
}
