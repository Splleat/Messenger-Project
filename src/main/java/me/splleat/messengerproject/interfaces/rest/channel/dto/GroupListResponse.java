package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.domain.group.Group;

public record GroupListResponse(
        long groupId,
        String groupName
) {
    public static GroupListResponse from(Group group) {
        return new GroupListResponse(
                group.getId(),
                group.getName()
        );
    }
}
