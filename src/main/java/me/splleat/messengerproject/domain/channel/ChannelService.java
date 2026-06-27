package me.splleat.messengerproject.domain.channel;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel register(Channel channel) {
        return channelRepository.save(channel);
    }

    @Transactional(readOnly = true)
    public Channel getChannel(long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Long> getSpaceChannelIds(long spaceId) {
        return channelRepository.findAllChannelIdBySpaceId(spaceId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getDirectChannels(List<Long> channelIds) {
        if (channelIds.isEmpty()) {
            return Collections.emptyList();
        }

        return channelRepository.findAllBySpaceIdIsNullAndIdIn(channelIds);
    }

    @Transactional(readOnly = true)
    public void validateInSpace(long spaceId, long channelId) {
        if (!channelRepository.existsByIdAndSpaceId(channelId, spaceId)) {
            throw new BusinessException(ErrorCode.SPACE_CHANNEL_NOT_FOUND);
        }
    }
}
