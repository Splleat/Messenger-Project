package me.splleat.messengerproject.domain.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.channel.exception.ChannelUserSettingAlreadyExistsException;
import me.splleat.messengerproject.domain.channel.exception.ChannelUserSettingNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelUserSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelUserSettingService {
    private final ChannelUserSettingRepository channelUserSettingRepository;

    @Transactional
    public ChannelUserSetting register(ChannelUserSetting channelUserSetting) {
        if (channelUserSettingRepository.existsByUserIdAndChannelId(channelUserSetting.getUserId(), channelUserSetting.getChannelId())) {
            throw new ChannelUserSettingAlreadyExistsException();
        }

        return channelUserSettingRepository.save(channelUserSetting);
    }

    @Transactional
    public void registerAll(List<ChannelUserSetting> channelUserSettingList) {

        if (channelUserSettingList == null || channelUserSettingList.isEmpty()) {
            return;
        }

        channelUserSettingRepository.saveAll(channelUserSettingList);
    }

    @Transactional(readOnly = true)
    public List<Long> getJoinedChannelIds(long userId) {
        return channelUserSettingRepository.findAllChannelIdByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Long> getJoinedUserIds(long channelId) {
        return channelUserSettingRepository.findAllUserIdByChannelId(channelId);
    }

    @Transactional(readOnly = true)
    public List<Long> alreadyJoinedIds(long channelId, List<Long> targetIds) {
        return channelUserSettingRepository.findAllUserIdByChannelIdAndUserIdIn(channelId, targetIds);
    }

    @Transactional(readOnly = true)
    public void validateParticipant(long userId, long channelId) {
        if (!channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new ChannelUserSettingNotFoundException();
        }
    }

    @Transactional
    public void leaveChannel(long userId, long channelId) {
        if (!channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new ChannelUserSettingNotFoundException();
        }

        channelUserSettingRepository.deleteByUserIdAndChannelId(userId, channelId);
    }

    @Transactional
    public void leaveChannels(long userId, List<Long> channelIds) {
        if (!channelIds.isEmpty()) {
            channelUserSettingRepository.deleteAllByUserIdAndChannelIdIn(userId, channelIds);
        }
    }

    @Transactional
    public void registerIfAbsent(long userId, long channelId) {
        if (!channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId)) {
            ChannelUserSetting channelUserSetting = ChannelUserSetting.create(userId, channelId);
            channelUserSettingRepository.save(channelUserSetting);
        }
    }
}

