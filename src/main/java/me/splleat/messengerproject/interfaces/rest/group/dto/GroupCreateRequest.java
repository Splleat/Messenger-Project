package me.splleat.messengerproject.interfaces.rest.group.dto;

import me.splleat.messengerproject.application.group.dto.GroupCreateCommand;

public record GroupCreateRequest(
        String groupName,
        String nickname
) {
    public GroupCreateCommand toCommand(long userId) {
        return new GroupCreateCommand(userId, groupName, nickname);
    }
}
