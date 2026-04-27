package me.splleat.messengerproject.interfaces.rest.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import me.splleat.messengerproject.application.group.dto.GroupCreateCommand;

public record GroupCreateRequest(
        @NotBlank(message = "그룹 이름은 필수 입력값입니다.")
        @Size(min = 1, max = 50, message = "그룹 이름은 1자 이상 50자 이하로 입력해야 합니다.")
        String groupName,

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해야 합니다.")
        String nickname
) {
    public GroupCreateCommand toCommand(long userId) {
        return new GroupCreateCommand(userId, nickname, groupName);
    }
}
