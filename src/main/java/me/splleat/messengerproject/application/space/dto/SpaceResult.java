package me.splleat.messengerproject.application.space.dto;

import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.domain.space.Space;

import java.util.List;

public record SpaceResult(
        long spaceId,
        String spaceName,
        List<ChannelListResult> channelList
) {
    public static SpaceResult of(Space space, List<ChannelListResult> channelList) {
        return new SpaceResult(
                space.getId(),
                space.getName(),
                channelList
        );
    }
}
