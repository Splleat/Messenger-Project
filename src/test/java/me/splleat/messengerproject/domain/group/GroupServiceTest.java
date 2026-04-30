package me.splleat.messengerproject.domain.group;

import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
}