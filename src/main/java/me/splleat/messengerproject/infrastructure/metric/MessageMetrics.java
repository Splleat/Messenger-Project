package me.splleat.messengerproject.infrastructure.metric;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageMetrics {
    public static final String EVENT_COUNTER = "message.event";
    public static final String SEND_DURATION = "message.send.duration";

    public static final String TAG_TYPE = "type";
    public static final String TAG_STATUS = "status";
}
