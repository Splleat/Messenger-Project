package me.splleat.messengerproject.infrastructure.metric;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import me.splleat.messengerproject.infrastructure.message.event.MessageDeletedEvent;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageMetricListenerTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final MessageMetricListener listener = new MessageMetricListener(meterRegistry);

    @Test
    @DisplayName("메시지 이벤트를 수신하면, 이벤트 타입 태그를 붙여 카운터를 1 증가시킨다.")
    void onMessageEvent_WhenEventReceived_IncrementsCounterWithTypeTag() {
        // given
        MessageEvent<MessageDeletedEvent> event = MessageEvent.from(new MessageDeletedEvent(1L, 1L));

        // when
        listener.onMessageEvent(event);

        // then
        double count = meterRegistry.counter(MessageMetrics.EVENT_COUNTER, MessageMetrics.TAG_TYPE, event.type().name())
                .count();

        assertThat(count)
                .isEqualTo(1.0);
    }
}
