package me.splleat.messengerproject.interfaces.rest.channel.dto;

import me.splleat.messengerproject.application.group.dto.GroupListResult;

public record GroupListResponse(
        long groupId,
        String groupName
) {
    public static GroupListResponse from(GroupListResult result) {
        return new GroupListResponse(
                result.groupId(),
                result.groupName()
        );
    }
}
