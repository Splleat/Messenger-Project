package me.splleat.messengerproject.application.group.dto;

import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;

import java.util.List;

public record GroupResult(
        long groupId,
        String groupName,
        List<ChannelListResult> channelList
) {
    public static GroupResult of(Group group, List<Channel> channels) {
        List<ChannelListResult> channelList = channels.stream()
                .map(ChannelListResult::from)
                .toList();

        return new GroupResult(
                group.getId(),
                group.getName(),
                channelList
        );
    }
}
