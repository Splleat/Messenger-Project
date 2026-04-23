package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelUserSettingRepository extends JpaRepository<ChannelUserSetting, Long> {
    boolean existsByUserIdAndChannelId(Long userId, Long channelId);

    List<Long> findAllUserIdByChannelId(Long channelId);
}
