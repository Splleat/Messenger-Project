package me.splleat.messengerproject.application.channel;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.ChannelQueryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ChannelListGetUseCase {
    private final ChannelQueryRepository channelQueryRepository;

    @Transactional(readOnly = true)
    public List<ChannelListResult> execute(long userId) {
        return channelQueryRepository.findDirectChannelList(userId);
    }
}
