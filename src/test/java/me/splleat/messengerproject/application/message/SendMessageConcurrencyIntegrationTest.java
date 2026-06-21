package me.splleat.messengerproject.application.message;

import me.splleat.messengerproject.application.message.dto.MessageCreateCommand;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.message.MessageType;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageOutboxRepository;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelRepository;
import me.splleat.messengerproject.infrastructure.persistence.jpa.ChannelUserSettingRepository;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MessageRepository;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserProfileRepository;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import me.splleat.messengerproject.support.TestContainerConfig;
import me.splleat.messengerproject.support.fixture.ChannelFixture;
import me.splleat.messengerproject.support.fixture.UserProfileFixture;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainerConfig.class)
class SendMessageConcurrencyIntegrationTest {

    @MockitoBean
    private StorageUrlMapper storageUrlMapper;

    @Autowired
    private SendMessageFacade sendMessageFacade;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelUserSettingRepository channelUserSettingRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private MessageOutboxRepository messageOutboxRepository;

    private static final long USER_ID = 1L;
    private long channelId;

    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        Channel channel = channelRepository.save(ChannelFixture.directChannel());
        channelId = channel.getId();
        ChannelUserSetting setting = ChannelUserSetting.create(USER_ID, channel.getId());
        channelUserSettingRepository.save(setting);
        UserProfile profile = UserProfileFixture.defaultUserProfile(USER_ID);
        userProfileRepository.save(profile);
    }

    @AfterEach
    void tearDown() {
        messageOutboxRepository.deleteAllInBatch();
        messageRepository.deleteAllInBatch();
        channelUserSettingRepository.deleteAllInBatch();
        channelRepository.deleteAllInBatch();
        userProfileRepository.deleteAllInBatch();

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @RepeatedTest(20)
    @DisplayName("동일한 멱등성 키로 동시에 메시지 전송을 요청하면, 메시지와 야웃박스 이벤트는 한 번만 생성된다.")
    void execute_WhenSendConcurrency_MessageAndOutboxCreatesOnce() throws Exception {
        // given
        int threadCount = 16;

        UUID idempotencyKey = UUID.randomUUID();
        executorService = Executors.newFixedThreadPool(threadCount);

        MessageCreateCommand command = new MessageCreateCommand(USER_ID, channelId, "test", idempotencyKey, MessageType.DIRECT, null, null);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<MessageResponse>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                }
                return sendMessageFacade.execute(command);
            }));
        }

        ready.await();
        start.countDown();

        long messageId = futures.getFirst().get().id();

        // then
        for (Future<MessageResponse> future : futures) {
            MessageResponse response = future.get();

            assertThat(response)
                    .isNotNull();
            assertThat(response.id())
                    .isEqualTo(messageId);
        }

        assertThat(messageRepository.count())
                .isEqualTo(1);
        assertThat(messageOutboxRepository.count())
                .isEqualTo(1);
    }
}
