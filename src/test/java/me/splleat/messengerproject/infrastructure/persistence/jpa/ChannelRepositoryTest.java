package me.splleat.messengerproject.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.domain.channel.Channel;
import me.splleat.messengerproject.domain.space.Space;
import me.splleat.messengerproject.support.annotation.ContainerDataJpaTest;
import me.splleat.messengerproject.support.fixture.ChannelFixture;
import me.splleat.messengerproject.support.fixture.SpaceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChannelRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ChannelRepository channelRepository;

    private static Space space;
    private final List<Channel> channelList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        space = SpaceFixture.defaultSpace();
        entityManager.persist(space);

        for (int i = 0; i < 5; i++) {
            Channel channel = ChannelFixture.spaceChannel(space.getId());

            entityManager.persist(channel);
            channelList.add(channel);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllChannelIdBySpaceId: 특정 그룹에 속한 채널 ID 목록을 반환한다.")
    void findAllChannelIdBySpaceId() {
        // given
        List<Long> expectedIds = channelList.stream()
                .map(Channel::getId)
                .toList();

        // when
        List<Long> found = channelRepository.findAllChannelIdBySpaceId(space.getId());

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(expectedIds);
    }
}