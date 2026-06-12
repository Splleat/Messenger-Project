package me.splleat.messengerproject.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.domain.channel.ChannelUserSetting;
import me.splleat.messengerproject.support.annotation.ContainerDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContainerDataJpaTest
class ChannelUserSettingRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ChannelUserSettingRepository channelUserSettingRepository;
    
    private static final long CHANNEL_ID = 1L;
    private static final long OTHER_CHANNEL_ID = 999L;
    
    private ChannelUserSetting setting1;
    private ChannelUserSetting setting2;
    private ChannelUserSetting setting3;
    
    @BeforeEach
    void setUp() {
        setting1 = ChannelUserSetting.create(10L, CHANNEL_ID);
        setting2 = ChannelUserSetting.create(20L, CHANNEL_ID);
        setting3 = ChannelUserSetting.create(30L, OTHER_CHANNEL_ID);

        entityManager.persist(setting1);
        entityManager.persist(setting2);
        entityManager.persist(setting3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllUserIdByChannelIdAndUserIdIn: 채널 ID와 사용자 ID 목록이 주어지면, 해당 채널에 참여 중인 사용자 ID만 반환한다.")
    void findAllUserIdByChannelIdAndUserIdIn() {
        // when
        List<Long> result = channelUserSettingRepository.findAllUserIdByChannelIdAndUserIdIn(
                CHANNEL_ID, 
                List.of(setting1.getUserId(), setting3.getUserId())
        );

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(setting1.getUserId());
    }

    @Test
    @DisplayName("findAllUserIdByChannelId: 채널 ID가 주어지면, 해당 채널에 참여 중인 모든 사용자 ID 목록을 반환한다.")
    void findAllUserIdByChannelId() {
        // when
        List<Long> result = channelUserSettingRepository.findAllUserIdByChannelId(CHANNEL_ID);

        // then
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(setting1.getUserId(), setting2.getUserId());
    }
}
