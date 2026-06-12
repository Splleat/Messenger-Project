package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.channel.dto.ChannelMessagePageResult;
import me.splleat.messengerproject.common.config.QueryDslConfig;
import me.splleat.messengerproject.domain.message.Message;
import me.splleat.messengerproject.domain.message.MessageType;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import me.splleat.messengerproject.support.fixture.UserProfileFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, MessageQueryRepository.class})
class MessageQueryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageQueryRepository messageQueryRepository;

    private static final long TEST_USER_ID = 1L;
    private static final long TEST_CHANNEL_ID = 100L;
    private final List<Message> savedMessages = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 프로필 생성
        UserProfile userProfile = UserProfileFixture.defaultUserProfile(TEST_USER_ID);
        entityManager.persist(userProfile);

        // 메시지 25개 생성
        for (int i = 0; i < 25; i++) {
            Message message = Message.create(
                    TEST_USER_ID,
                    TEST_CHANNEL_ID,
                    "메시지 내용 " + i,
                    MessageType.SPACE,
                    null,
                    UUID.randomUUID(),
                    Collections.emptyList()
            );
            entityManager.persist(message);
            savedMessages.add(message);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findByNewest: 최신 메시지 페이징 조회 시 가장 최근 20개 메시지를 역순으로 조회하고 hasMore가 true여야 한다")
    void findByNewest() {
        // when
        ChannelEnterResult result = messageQueryRepository.findByNewest(TEST_CHANNEL_ID);

        // then
        assertThat(result)
                .isNotNull();

        List<MessageResponse> messages = result.messages();
        
        assertThat(messages)
                .hasSize(20);
        assertThat(result.hasPrev())
                .isTrue();

        // savedMessage 5 ~ 24 메시지 검증
        assertThat(messages.getFirst().content())
                .isEqualTo("메시지 내용 5");
        assertThat(messages.getLast().content())
                .isEqualTo("메시지 내용 24");
        
        assertThat(messages.getFirst().username())
                .isEqualTo(UserProfileFixture.NAME);
    }

    @Test
    @DisplayName("findByPrevId: 15번째 메시지 커서 이전의 메시지 목록을 역순으로 조회하고 hasMore이 false여야 한다.")
    void findByPrevId() {
        // given
        // 15번째 메시지를 커서로 설정
        long cursorId = savedMessages.get(14).getId();

        // when
        // 15번째 메시지 이전 20개 메시지 조회 -> 0 ~ 14번째 메시지 조회
        ChannelMessagePageResult result = messageQueryRepository.findByPrevId(TEST_CHANNEL_ID, cursorId);

        // then
        assertThat(result)
                .isNotNull();

        List<MessageResponse> messages = result.messages();

        // savedMessage 0 ~ 13 메시지 검증
        assertThat(messages)
                .hasSize(14);
        assertThat(result.hasMore())
                .isFalse();
        assertThat(messages.getFirst().content())
                .isEqualTo("메시지 내용 0");
        assertThat(messages.get(13).content())
                .isEqualTo("메시지 내용 13");
    }

    @Test
    @DisplayName("findByNextId: 3번째 메시지 커서 이후의 메시지 목록을 역순으로 조회하고 hasMore이 true여야 한다")
    void findByNextId() {
        // given
        // 3번째 메시지를 커서로 설정
        long cursorId = savedMessages.get(2).getId();

        // when
        // 3번째 메시지 이후 20개 메시지 조회 -> 4 ~ 23번째 메시지 조회
        ChannelMessagePageResult result = messageQueryRepository.findByNextId(TEST_CHANNEL_ID, cursorId);

        // then
        assertThat(result)
                .isNotNull();

        List<MessageResponse> messages = result.messages();

        // savedMessage 3 ~ 22 메시지 검증
        assertThat(messages)
                .hasSize(20);
        assertThat(result.hasMore())
                .isTrue();
        assertThat(messages.getFirst().content())
                .isEqualTo("메시지 내용 3");
        assertThat(messages.getLast().content())
                .isEqualTo("메시지 내용 22");
    }

    @Test
    @DisplayName("findByAroundId: 10번째 메시지 커서 앞뒤 메시지를 조회하여 병합해야 한다.")
    void findByAroundId() {
        // given
        // 10번째 메시지를 커서로 설정
        long lastReadMessageId = savedMessages.get(9).getId();

        List<String> expectedContents = savedMessages.stream()
                .map(Message::getContent)
                .toList();

        // when
        ChannelEnterResult result = messageQueryRepository.findByAroundId(TEST_CHANNEL_ID, lastReadMessageId);

        // then
        assertThat(result)
                .isNotNull();

        List<MessageResponse> messages = result.messages();

        // lastReadMessageId(9)를 기준으로 lt(0 ~ 8) + goe(9 ~ 24)
        // 이전: 1~9번째 메시지 (9개)
        // 이후: 10~25번째 메시지 (16개)
        assertThat(messages)
                .isNotEmpty()
                .hasSize(25)
                .extracting(MessageResponse::content)
                .containsExactlyInAnyOrderElementsOf(expectedContents);

        assertThat(messages.getFirst().content())
                .isEqualTo("메시지 내용 0");
        assertThat(messages.getLast().content())
                .isEqualTo("메시지 내용 24");
    }
}