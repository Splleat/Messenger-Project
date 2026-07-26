package me.splleat.messengerproject.infrastructure.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class MessageMetricRecorder {
    private final MeterRegistry meterRegistry;

    public <T> T recordSendDuration(Supplier<T> action) {
        return Timer.builder(MessageMetrics.SEND_DURATION)
                .register(meterRegistry)
                .record(action);
    }
}
