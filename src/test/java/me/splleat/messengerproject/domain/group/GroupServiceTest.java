package me.splleat.messengerproject.domain.group;

import me.splleat.messengerproject.domain.group.exception.GroupNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("그룹을 등록하면, 등록된 그룹의 정보가 반환된다.")
    void register_WhenValid_ReturnsGroup() {
        // given
        Group group = Group.create("test");

        given(groupRepository.save(group))
                .willReturn(group);

        // when
        Group found = groupService.register(group);

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(group);
    }
    
    @Test
    @DisplayName("요청 그룹 ID 목록이 비어 있으면, 빈 리스트가 반환된다.")
    void getGroups_WhenEmptyList_ReturnsEmptyList() {
        // given
        List<Long> groupIds = List.of();

        // when
        List<Group> found = groupService.getGroups(groupIds);

        // then
        assertThat(found)
                .isEmpty();
    }

    @Test
    @DisplayName("요청 그룹 ID 목록이 주어지면, 해당하는 그룹 목록을 반환한다.")
    void getGroups_WhenValidIds_ReturnsGroups() {
        // given
        List<Long> groupIds = List.of(1L, 2L);
        List<Group> groups = List.of(Group.create("group1"), Group.create("group2"));

        given(groupRepository.findAllByIdIn(groupIds))
                .willReturn(groups);

        // when
        List<Group> result = groupService.getGroups(groupIds);

        // then
        assertThat(result)
                .hasSize(2)
                .isEqualTo(groups);
    }

    @Test
    @DisplayName("그룹 ID로 그룹을 조회하면, 해당 그룹이 반환된다.")
    void getGroup_WhenExists_ReturnsGroup() {
        // given
        long groupId = 1L;
        Group group = Group.create("test");

        given(groupRepository.findById(groupId))
                .willReturn(Optional.of(group));

        // when
        Group found = groupService.getGroup(groupId);

        // then
        assertThat(found)
                .isEqualTo(group);
    }

    @Test
    @DisplayName("존재하지 않는 그룹을 요청하려 하면, GroupNotFoundException이 발생한다.")
    void getGroup_WhenNotExists_ThrowsException() {
        // given
        long groupId = 1L;
        
        given(groupRepository.findById(groupId))
                .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> groupService.getGroup(groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }
}