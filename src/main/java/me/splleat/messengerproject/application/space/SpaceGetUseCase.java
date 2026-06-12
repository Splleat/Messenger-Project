package me.splleat.messengerproject.application.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.application.space.dto.SpaceResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.domain.space.Space;
import me.splleat.messengerproject.domain.space.SpaceService;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.ChannelQueryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SpaceGetUseCase {
    private final SpaceService spaceService;
    private final SpaceMemberService spaceMemberService;
    private final ChannelQueryRepository channelQueryRepository;

    @Transactional(readOnly = true)
    public SpaceResult execute(long userId, long spaceId) {
        spaceMemberService.validateParticipant(userId, spaceId);

        Space space = spaceService.getSpace(spaceId);

        List<ChannelListResult> spaceChannels = channelQueryRepository.findSpaceChannelList(userId, spaceId);

        return SpaceResult.of(space, spaceChannels);
    }
}
