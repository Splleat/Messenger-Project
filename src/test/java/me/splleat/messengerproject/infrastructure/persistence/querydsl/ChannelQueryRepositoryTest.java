package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantResult;
import me.splleat.messengerproject.common.config.QueryDslConfig;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupRole;
import me.splleat.messengerproject.domain.profile.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, ChannelQueryRepository.class})
class ChannelQueryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ChannelQueryRepository channelQueryRepository;

    private static final long TEST_USER_ID = 1L;
    private static final long OTHER_USER_ID = 2L;
    private Group testGroup;
    private Channel directChannel;
    private Channel groupChannel;

    @BeforeEach
    void setUp() {
        // 유저 프로필 생성
        UserProfile user1 = UserProfile.create(TEST_USER_ID, "testUser");
        UserProfile user2 = UserProfile.create(OTHER_USER_ID, "otherUser");
        entityManager.persist(user1);
        entityManager.persist(user2);

        // 다이렉트 채널 생성
        directChannel = Channel.createDirectChannel("Direct Channel", ChannelType.TEXT);
        entityManager.persist(directChannel);

        // 다이렉트 채널 참여 설정
        entityManager.persist(ChannelUserSetting.create(TEST_USER_ID, directChannel.getId()));
        entityManager.persist(ChannelUserSetting.create(OTHER_USER_ID, directChannel.getId()));

        // 그룹 생성
        testGroup = Group.create("Test Group");
        entityManager.persist(testGroup);

        // 그룹 멤버 설정
        entityManager.persist(GroupMember.create(TEST_USER_ID, testGroup.getId(), "nick1", GroupRole.MEMBER));

        // 그룹 채널 생성
        groupChannel = Channel.createGroupChannel(testGroup.getId(), "Group Channel", ChannelType.TEXT);
        entityManager.persist(groupChannel);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findChannelParticipant: 해당 채널의 참여자 목록을 반환한다.")
    void findChannelParticipant() {
        // when
        List<ChannelParticipantResult> participants = channelQueryRepository.findChannelParticipant(directChannel.getId());

        // then
        assertThat(participants)
                .hasSize(2);
        assertThat(participants)
                .extracting(ChannelParticipantResult::userId)
                .containsExactlyInAnyOrder(TEST_USER_ID, OTHER_USER_ID);
    }

    @Test
    @DisplayName("findDirectChannelList: 해당 유저의 다이렉트 채널 목록을 반환한다.")
    void findDirectChannelList() {
        // when
        List<ChannelListResult> results = channelQueryRepository.findDirectChannelList(TEST_USER_ID);

        // then
        assertThat(results)
                .hasSize(1);
        assertThat(results.getFirst().channelId())
                .isEqualTo(directChannel.getId());
        assertThat(results.getFirst().channelName())
                .isEqualTo(directChannel.getName());
    }

    @Test
    @DisplayName("findGroupChannelList: 해당 유저가 속한 특정 그룹의 채널 목록을 반환한다.")
    void findGroupChannelList() {
        // when
        List<ChannelListResult> results = channelQueryRepository.findGroupChannelList(TEST_USER_ID, testGroup.getId());

        // then
        assertThat(results)
                .hasSize(1);
        assertThat(results.getFirst().channelId())
                .isEqualTo(groupChannel.getId());
        assertThat(results.getFirst().channelName())
                .isEqualTo(groupChannel.getName());
    }
}
