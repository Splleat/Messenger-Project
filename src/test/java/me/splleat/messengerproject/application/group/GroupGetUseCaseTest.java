package me.splleat.messengerproject.application.group;

import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.application.group.dto.GroupResult;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.domain.group.GroupService;
import me.splleat.messengerproject.domain.group.GroupMemberService;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.ChannelQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GroupGetUseCaseTest {

    @Mock
    private GroupService groupService;

    @Mock
    private GroupMemberService groupMemberService;

    @Mock
    private ChannelQueryRepository channelQueryRepository;

    @InjectMocks
    private GroupGetUseCase groupGetUseCase;

    @Test
    @DisplayName("사용자가 그룹 참여자라면 그룹 정보와 채널 목록을 반환한다.")
    void execute_WhenUserIsParticipant_ReturnsGroupResult() {
        // given
        long userId = 1L;
        long groupId = 10L;
        Group group = mock(Group.class);
        List<ChannelListResult> channels = List.of(new ChannelListResult(100L, "General", false));

        given(groupService.getGroup(groupId))
                .willReturn(group);
        given(channelQueryRepository.findGroupChannelList(userId, groupId))
                .willReturn(channels);

        // when
        GroupResult result = groupGetUseCase.execute(userId, groupId);

        // then
        then(groupMemberService)
                .should().
                validateParticipant(userId, groupId);

        assertThat(result)
                .isNotNull()
                .extracting("groupName")
                .isEqualTo(group.getName());

        assertThat(result.channelList())
                .isNotNull()
                .isEqualTo(channels);
    }
}
