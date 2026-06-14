package me.splleat.messengerproject.application.space;

import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.application.space.dto.SpaceResult;
import me.splleat.messengerproject.domain.space.Space;
import me.splleat.messengerproject.domain.space.SpaceService;
import me.splleat.messengerproject.domain.space.SpaceMemberService;
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
class SpaceGetUseCaseTest {

    @Mock
    private SpaceService spaceService;

    @Mock
    private SpaceMemberService spaceMemberService;

    @Mock
    private ChannelQueryRepository channelQueryRepository;

    @InjectMocks
    private SpaceGetUseCase spaceGetUseCase;

    @Test
    @DisplayName("사용자가 그룹 참여자라면 그룹 정보와 채널 목록을 반환한다.")
    void execute_WhenUserIsParticipant_ReturnsSpaceResult() {
        // given
        long userId = 1L;
        long spaceId = 10L;
        Space space = mock(Space.class);
        List<ChannelListResult> channels = List.of(new ChannelListResult(100L, "General", false));

        given(spaceService.getSpace(spaceId))
                .willReturn(space);
        given(channelQueryRepository.findSpaceChannelList(userId, spaceId))
                .willReturn(channels);

        // when
        SpaceResult result = spaceGetUseCase.execute(userId, spaceId);

        // then
        then(spaceMemberService)
                .should().
                validateParticipant(userId, spaceId);

        assertThat(result)
                .isNotNull()
                .extracting("spaceName")
                .isEqualTo(space.getName());

        assertThat(result.channelList())
                .isNotNull()
                .isEqualTo(channels);
    }
}
