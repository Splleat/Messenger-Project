package me.splleat.messengerproject.infrastructure.metric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.message.event.MessageEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMetricListener {
    private final MeterRegistry meterRegistry;

    @EventListener
    public void onMessageEvent(MessageEvent<?> event) {
        meterRegistry.counter(MessageMetrics.EVENT_COUNTER,
                MessageMetrics.TAG_TYPE, event.type().name())
                .increment();
    }
}
