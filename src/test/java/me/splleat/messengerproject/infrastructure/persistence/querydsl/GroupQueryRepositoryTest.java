package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import jakarta.persistence.EntityManager;
import me.splleat.messengerproject.application.group.dto.GroupListResult;
import me.splleat.messengerproject.common.config.QueryDslConfig;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupMember;
import me.splleat.messengerproject.domain.group.GroupRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, GroupQueryRepository.class})
class GroupQueryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GroupQueryRepository groupQueryRepository;

    private static final long USER_ID = 1L;
    private final List<Group> groupList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 10; i++) {
            Group group = Group.create("그룹" + i);
            entityManager.persist(group);

            GroupMember member = GroupMember.create(USER_ID, group.getId(), "test", GroupRole.MEMBER);
            entityManager.persist(member);

            groupList.add(group);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findGroupList: 사용자가 속한 그룹 목록을 조회한다.")
    void findGroupList() {
        // given
        List<String> expectedNames = groupList.stream()
                .map(Group::getName)
                .toList();

        // when
        List<GroupListResult> found = groupQueryRepository.findGroupList(USER_ID);

        // then
        assertThat(found)
                .isNotNull()
                .hasSize(groupList.size())
                .extracting(GroupListResult::groupName)
                .containsExactlyInAnyOrderElementsOf(expectedNames);
    }
}