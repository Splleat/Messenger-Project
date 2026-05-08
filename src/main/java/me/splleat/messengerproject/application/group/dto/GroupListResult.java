package me.splleat.messengerproject.application.group.dto;

import me.splleat.messengerproject.domain.group.Group;

public record GroupListResult(
        long groupId,
        String groupName
) {
    public static GroupListResult from(Group group) {
        return new GroupListResult(group.getId(), group.getName());
    }
}
