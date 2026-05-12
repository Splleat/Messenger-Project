package me.splleat.messengerproject.interfaces.rest.group.dto;

import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelListResponse;

import java.util.List;

public record GroupResponse(
        long groupId,
        String groupName,
        List<ChannelListResponse> channelList
) {
    public static GroupResponse of(Group group, List<Channel> channels) {
        List<ChannelListResponse> channelList = channels.stream()
                .map(ChannelListResponse::from)
                .toList();

        return new GroupResponse(
                group.getId(),
                group.getName(),
                channelList
        );
    }
}
