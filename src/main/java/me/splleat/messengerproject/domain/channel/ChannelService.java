package me.splleat.messengerproject.domain.channel;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.channel.exception.ChannelNotFoundException;
import me.splleat.messengerproject.domain.channel.exception.GroupChannelNotFoundException;
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
                .orElseThrow(ChannelNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Long> getGroupChannelIds(long groupId) {
        return channelRepository.findAllChannelIdByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getGroupChannels(long groupId) {
        return channelRepository.findAllByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getDirectChannels(List<Long> channelIds) {
        if (channelIds.isEmpty()) {
            return Collections.emptyList();
        }

        return channelRepository.findAllByGroupIdIsNullAndIdIn(channelIds);
    }

    @Transactional(readOnly = true)
    public void validateInGroup(long groupId, long channelId) {
        if (!channelRepository.existsByIdAndGroupId(channelId, groupId)) {
            throw new GroupChannelNotFoundException();
        }
    }
}
