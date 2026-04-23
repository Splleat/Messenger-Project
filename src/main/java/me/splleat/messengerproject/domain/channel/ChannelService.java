package me.splleat.messengerproject.domain.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.channel.exception.ChannelNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
