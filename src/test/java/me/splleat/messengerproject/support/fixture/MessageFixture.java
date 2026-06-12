package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;

import java.util.UUID;

public final class MessageFixture {
    public static Message defaultMessage(long userId, long channelId) {
        return Message.create(userId, channelId, "test", MessageType.DIRECT, null, UUID.randomUUID(), null);
    }
}
