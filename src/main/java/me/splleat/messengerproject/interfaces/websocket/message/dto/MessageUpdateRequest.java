package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.application.message.dto.MessageUpdateCommand;

public record MessageUpdateRequest(
        String content
) {
    public MessageUpdateCommand toCommand(long userId, long messageId) {
        return new MessageUpdateCommand(userId, messageId, content);
    }
}
