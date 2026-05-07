package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChannelUserSettingRepository extends JpaRepository<ChannelUserSetting, Long> {

    boolean existsByUserIdAndChannelId(Long userId, Long channelId);

    void deleteAllByUserIdAndChannelIdIn(Long userId, Collection<Long> channelIds);

    List<Long> findAllUserIdByChannelIdAndUserIdIn(Long channelId, Collection<Long> userIds);

    List<ChannelUserSetting> findAllByUserIdAndChannelIdIn(Long userId, Collection<Long> channelIds);

    List<ChannelUserSetting> findAllByChannelIdAndUserIdIn(Long channelId, Collection<Long> userIds);

    void deleteByUserIdAndChannelId(Long userId, Long channelId);
}
