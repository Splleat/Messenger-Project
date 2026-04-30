package me.splleat.messengerproject.interfaces.rest.channel.dto;

import jakarta.validation.constraints.NotEmpty;
import me.splleat.messengerproject.application.channel.dto.DirectChannelInviteCommand;

import java.util.List;

public record DirectChannelInviteRequest(
        @NotEmpty(message = "초대할 대상은 필수 입력값입니다.")
        List<Long> targetIds
) {
    public DirectChannelInviteCommand toCommand(long userId, long channelId) {
        return new DirectChannelInviteCommand(userId, channelId, targetIds);
    }
}
