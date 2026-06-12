package me.splleat.messengerproject.interfaces.rest.space.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import me.splleat.messengerproject.application.space.dto.SpaceCreateCommand;

public record SpaceCreateRequest(
        @NotBlank(message = "그룹 이름은 필수 입력값입니다.")
        @Size(min = 1, max = 50, message = "그룹 이름은 1자 이상 50자 이하로 입력해야 합니다.")
        String spaceName
) {
    public SpaceCreateCommand toCommand(long userId) {
        return new SpaceCreateCommand(userId, spaceName);
    }
}
