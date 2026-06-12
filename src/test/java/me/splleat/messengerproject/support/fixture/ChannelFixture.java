package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelType;

public final class ChannelFixture {
    public static Channel directChannel() {
        return Channel.createDirectChannel("testChannel", ChannelType.TEXT);
    }

    public static Channel spaceChannel(long spaceId) {
        return Channel.createSpaceChannel(spaceId, "testChannel", ChannelType.TEXT);
    }
}
