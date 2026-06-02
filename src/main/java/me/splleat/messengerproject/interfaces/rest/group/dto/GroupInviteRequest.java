package me.splleat.messengerproject.interfaces.rest.group.dto;

import jakarta.validation.constraints.NotEmpty;
import me.splleat.messengerproject.application.group.dto.GroupInviteCommand;

import java.util.List;

public record GroupInviteRequest(
        @NotEmpty
        List<Long> targetIds
) {
    public GroupInviteCommand toCommand(long userId, long groupId) {
        return new GroupInviteCommand(userId, groupId, targetIds);
    }
}
