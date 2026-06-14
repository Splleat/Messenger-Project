package me.splleat.messengerproject.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.domain.space.Space;
import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceRole;
import me.splleat.messengerproject.support.annotation.ContainerDataJpaTest;
import me.splleat.messengerproject.support.fixture.SpaceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ContainerDataJpaTest
class SpaceMemberRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SpaceMemberRepository spaceMemberRepository;

    private Space space;
    private SpaceMember member1;
    private SpaceMember member2;

    @BeforeEach
    void setUp() {
        space = SpaceFixture.defaultSpace();
        entityManager.persist(space);

        member1 = SpaceMember.create(1L, space.getId(), "nick1", SpaceRole.OWNER);
        member2 = SpaceMember.create(2L, space.getId(), "nick2", SpaceRole.MEMBER);

        entityManager.persist(member1);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllUserIdBySpaceId: 특정 그룹의 모든 사용자 ID를 조회한다.")
    void findAllUserIdBySpaceId() {
        // when
        List<Long> userIds = spaceMemberRepository.findAllUserIdBySpaceId(space.getId());

        // then
        assertThat(userIds)
                .hasSize(2)
                .containsExactlyInAnyOrder(member1.getUserId(), member2.getUserId());
    }

    @Test
    @DisplayName("findNicknameByUserIdAndSpaceId: 특정 그룹 내 사용자의 닉네임을 조회한다.")
    void findNicknameByUserIdAndSpaceId() {
        // when
        Optional<String> nickname = spaceMemberRepository.findNicknameByUserIdAndSpaceId(member1.getUserId(), space.getId());

        // then
        assertThat(nickname)
                .isPresent()
                .contains(member1.getNickname());
    }

    @Test
    @DisplayName("findAllSpaceIdByUserId: 사용자가 속한 모든 그룹 ID를 조회한다.")
    void findAllSpaceIdByUserId() {
        // when
        List<Long> spaceIds = spaceMemberRepository.findAllSpaceIdByUserId(member1.getUserId());

        // then
        assertThat(spaceIds)
                .hasSize(1)
                .containsExactly(space.getId());
    }

    @Test
    @DisplayName("findAllUserIdBySpaceIdAndUserIdIn: 그룹 내 특정 사용자 목록 중 존재하는 사용자 ID만 조회한다.")
    void findAllUserIdBySpaceIdAndUserIdIn() {
        // given
        List<Long> targetUserIds = List.of(member1.getUserId(), 999L);

        // when
        List<Long> result = spaceMemberRepository.findAllUserIdBySpaceIdAndUserIdIn(space.getId(), targetUserIds);

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(member1.getUserId());
    }
}
