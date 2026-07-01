package me.splleat.messengerproject.infrastructure.message.event;

import me.splleat.messengerproject.infrastructure.message.outbox.MessageEventType;
import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import tools.jackson.databind.json.JsonMapper;

public record MessageEvent<T>(
        long messageId,
        long channelId,
        MessageEventType type,
        T data
) {
    public static MessageEvent<MessageCreatedEvent> from(MessageCreatedEvent event) {
        return new MessageEvent<>(event.message().id(), event.message().channelId(), MessageEventType.CREATED, event);
    }

    public static MessageEvent<MessageUpdatedEvent> from(MessageUpdatedEvent event) {
        return new MessageEvent<>(event.messageId(), event.channelId(), MessageEventType.UPDATED, event);
    }

    public static MessageEvent<MessageDeletedEvent> from(MessageDeletedEvent event) {
        return new MessageEvent<>(event.messageId(), event.channelId(), MessageEventType.DELETED, event);
    }

    public String toJson(JsonMapper jsonMapper) {
        return jsonMapper.writeValueAsString(this);
    }

    public MessageOutbox toOutbox(JsonMapper jsonMapper) {
        return MessageOutbox.create(messageId, channelId, toJson(jsonMapper));
    }
}
