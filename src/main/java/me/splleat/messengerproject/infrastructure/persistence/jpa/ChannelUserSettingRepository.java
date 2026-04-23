package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelUserSettingRepository extends JpaRepository<ChannelUserSetting, Long> {
}
