package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.common.config.QueryDslConfig;
import me.splleat.messengerproject.domain.space.Space;
import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceRole;
import me.splleat.messengerproject.support.annotation.ContainerDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContainerDataJpaTest
@Import({QueryDslConfig.class, SpaceQueryRepository.class})
class SpaceQueryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SpaceQueryRepository spaceQueryRepository;

    private static final long USER_ID = 1L;
    private final List<Space> spaceList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 10; i++) {
            Space space = Space.create("그룹" + i);
            entityManager.persist(space);

            SpaceMember member = SpaceMember.create(USER_ID, space.getId(), "test", SpaceRole.MEMBER);
            entityManager.persist(member);

            spaceList.add(space);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findSpaceList: 사용자가 속한 그룹 목록을 조회한다.")
    void findSpaceList() {
        // given
        List<String> expectedNames = spaceList.stream()
                .map(Space::getName)
                .toList();

        // when
        List<SpaceListResult> found = spaceQueryRepository.findSpaceList(USER_ID);

        // then
        assertThat(found)
                .isNotNull()
                .hasSize(spaceList.size())
                .extracting(SpaceListResult::spaceName)
                .containsExactlyInAnyOrderElementsOf(expectedNames);
    }
}