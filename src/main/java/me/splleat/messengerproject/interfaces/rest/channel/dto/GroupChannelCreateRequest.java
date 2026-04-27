package me.splleat.messengerproject.interfaces.rest.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.splleat.messengerproject.application.channel.dto.GroupChannelCreateCommand;
import me.splleat.messengerproject.domain.channel.ChannelType;

public record GroupChannelCreateRequest(
        @NotBlank(message = "채널 이름은 필수 입력값입니다.")
        @Size(min = 1, max = 50, message = "채널 이름은 1자 이상 50자 이하로 입력해야 합니다.")
        String channelName,

        @NotNull(message = "채널 타입은 필수 입력값입니다.")
        ChannelType type
) {
    public GroupChannelCreateCommand toCommand(long userId, long groupId) {
        return new GroupChannelCreateCommand(userId, groupId, channelName, type);
    }
}
