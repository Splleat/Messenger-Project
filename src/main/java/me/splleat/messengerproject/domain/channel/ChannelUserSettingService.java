package me.splleat.messengerproject.domain.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
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
            throw new BusinessException(ErrorCode.CHANNEL_USER_SETTING_ALREADY_EXISTS);
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
    public ChannelUserSetting getChannelUserSetting(long userId, long channelId) {
        return channelUserSettingRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Long> alreadyJoinedIds(long channelId, List<Long> targetIds) {
        return channelUserSettingRepository.findAllUserIdByChannelIdAndUserIdIn(channelId, targetIds);
    }

    @Transactional(readOnly = true)
    public void validateParticipant(long userId, long channelId) {
        if (!channelUserSettingRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new BusinessException(ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND);
        }
    }

    @Transactional
    public void leaveChannel(long userId, long channelId) {
        ChannelUserSetting setting = channelUserSettingRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_USER_SETTING_NOT_FOUND));

        channelUserSettingRepository.delete(setting);
    }

    @Transactional
    public void leaveChannels(long userId, List<Long> channelIds) {
        if (!channelIds.isEmpty()) {
            channelUserSettingRepository.deleteAllByUserIdAndChannelIdIn(userId, channelIds);
        }
    }

    @Transactional
    public ChannelUserSetting registerIfAbsent(long userId, long channelId) {
        return channelUserSettingRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseGet(() -> {
                    ChannelUserSetting channelUserSetting = ChannelUserSetting.create(userId, channelId);
                    return channelUserSettingRepository.save(channelUserSetting);
                });
    }
}

