package me.splleat.messengerproject.infrastructure.message.relay;

import me.splleat.messengerproject.infrastructure.message.outbox.MessageOutbox;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageOutboxRepository;
import me.splleat.messengerproject.support.TestContainerConfig;
import me.splleat.messengerproject.support.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@IntegrationTest
@Import(TestContainerConfig.class)
class MessageRelaySchedulerIntegrationTest {

    @Autowired
    private MessageOutboxRepository messageOutboxRepository;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private ChannelTopic messageTopic;

    @Autowired
    private MessageRelayScheduler messageRelayScheduler;

    private final List<String> received = new CopyOnWriteArrayList<>();
    private MessageListener testListener;

    @BeforeEach
    void setUp() {
        received.clear();
        testListener = (message, pattern) -> received.add(message.toString());
        redisMessageListenerContainer.addMessageListener(testListener, messageTopic);
    }

    @AfterEach
    void tearDown() {
        redisMessageListenerContainer.removeMessageListener(testListener);
        messageOutboxRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("윈도우 안의 미처리 아웃박스는 릴레이가 재발행하고 processed = true로 마킹한다.")
    void relay_WhenUnprocessedAndInWindow_RepublishesAndMarkToProcessed() {
        // given
        ReflectionTestUtils.setField(messageRelayScheduler, "graceSeconds", 0);

        MessageOutbox outbox = messageOutboxRepository.save(MessageOutbox.create(1L, 1L, "{\"channelId\":1,\"content\":\"hi\"}"));

        // when
        messageRelayScheduler.relay();

        // then
        await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> assertThat(received).anyMatch(p -> p.contains("hi")));

        MessageOutbox reloaded = messageOutboxRepository.findById(outbox.getId())
                .orElseThrow();

        assertThat(reloaded.isProcessed())
                .isTrue();
    }

    @Test
    @DisplayName("유예 기간보다 빠르게 생성된 아웃박스는 릴레이가 재발행하지 않는다")
    void relay_WhenOutboxInGracePeriod_SkipsPublishing() {
        // given
        ReflectionTestUtils.setField(messageRelayScheduler, "graceSeconds", 2);

        MessageOutbox outbox = messageOutboxRepository.save(MessageOutbox.create(2L, 1L, "{\"channelId\":1,\"content\":\"hi\"}"));

        // when
        messageRelayScheduler.relay();

        // then
        MessageOutbox found = messageOutboxRepository.findById(outbox.getId())
                .orElseThrow();

        assertThat(found.isProcessed())
                .isFalse();
        assertThat(received)
                .isEmpty();
    }
}