package me.splleat.messengerproject.infrastructure.metric;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageMetricRecorderTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final MessageMetricRecorder recorder = new MessageMetricRecorder(meterRegistry);

    @Test
    @DisplayName("작업을 기록하면, 작업의 반환값을 그대로 반환하고 소요시간 타이머를 1회 기록한다.")
    void recordSendDuration_WhenActionSucceeds_ReturnsResultAndRecordsTimer() {
        // when
        String result = recorder.recordSendDuration(() -> "ok");

        // then
        assertThat(result)
                .isEqualTo("ok");

        long count = meterRegistry.timer(MessageMetrics.SEND_DURATION).count();
        assertThat(count)
                .isEqualTo(1);
    }
}
