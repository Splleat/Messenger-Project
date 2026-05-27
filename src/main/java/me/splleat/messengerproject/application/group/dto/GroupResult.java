package me.splleat.messengerproject.application.group.dto;

import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.domain.group.Group;

import java.util.List;

public record GroupResult(
        long groupId,
        String groupName,
        List<ChannelListResult> channelList
) {
    public static GroupResult of(Group group, List<ChannelListResult> channelList) {
        return new GroupResult(
                group.getId(),
                group.getName(),
                channelList
        );
    }
}
