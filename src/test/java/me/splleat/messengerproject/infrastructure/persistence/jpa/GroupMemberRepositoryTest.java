package me.splleat.messengerproject.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupRole;
import me.splleat.messengerproject.support.fixture.GroupFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GroupMemberRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    private Group group;
    private GroupMember member1;
    private GroupMember member2;

    @BeforeEach
    void setUp() {
        group = GroupFixture.defaultGroup();
        entityManager.persist(group);

        member1 = GroupMember.create(1L, group.getId(), "nick1", GroupRole.OWNER);
        member2 = GroupMember.create(2L, group.getId(), "nick2", GroupRole.MEMBER);

        entityManager.persist(member1);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllUserIdByGroupId: 특정 그룹의 모든 사용자 ID를 조회한다.")
    void findAllUserIdByGroupId() {
        // when
        List<Long> userIds = groupMemberRepository.findAllUserIdByGroupId(group.getId());

        // then
        assertThat(userIds)
                .hasSize(2)
                .containsExactlyInAnyOrder(member1.getUserId(), member2.getUserId());
    }

    @Test
    @DisplayName("findNicknameByUserIdAndGroupId: 특정 그룹 내 사용자의 닉네임을 조회한다.")
    void findNicknameByUserIdAndGroupId() {
        // when
        Optional<String> nickname = groupMemberRepository.findNicknameByUserIdAndGroupId(member1.getUserId(), group.getId());

        // then
        assertThat(nickname)
                .isPresent()
                .contains(member1.getNickname());
    }

    @Test
    @DisplayName("findAllGroupIdByUserId: 사용자가 속한 모든 그룹 ID를 조회한다.")
    void findAllGroupIdByUserId() {
        // when
        List<Long> groupIds = groupMemberRepository.findAllGroupIdByUserId(member1.getUserId());

        // then
        assertThat(groupIds)
                .hasSize(1)
                .containsExactly(group.getId());
    }

    @Test
    @DisplayName("findAllUserIdByGroupIdAndUserIdIn: 그룹 내 특정 사용자 목록 중 존재하는 사용자 ID만 조회한다.")
    void findAllUserIdByGroupIdAndUserIdIn() {
        // given
        List<Long> targetUserIds = List.of(member1.getUserId(), 999L);

        // when
        List<Long> result = groupMemberRepository.findAllUserIdByGroupIdAndUserIdIn(group.getId(), targetUserIds);

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(member1.getUserId());
    }
}
