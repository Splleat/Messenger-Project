package me.splleat.messengerproject.infrastructure.message.outbox;

import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import tools.jackson.databind.json.JsonMapper;

public record MessageCreateEvent(
        MessageResponse message
) {
    public static MessageCreateEvent from(MessageResponse message) {
        return new MessageCreateEvent(message);
    }

    public String toJson(JsonMapper jsonMapper) {
        return jsonMapper.writeValueAsString(message);
    }

    public MessageOutbox toOutbox(JsonMapper jsonMapper) {
        return MessageOutbox.create(message.id(), message.channelId(), toJson(jsonMapper));
    }
}
