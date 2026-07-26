package me.splleat.messengerproject.application.space;

import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.SpaceQueryRepository;
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
class SpaceListGetUseCaseTest {

    @Mock
    private SpaceQueryRepository spaceQueryRepository;

    @InjectMocks
    private SpaceListGetUseCase spaceListGetUseCase;

    @Test
    @DisplayName("스페이스 목록 조회 요청 시, 리포지토리를 통해 해당 사용자의 스페이스 목록을 반환한다.")
    void execute_ReturnsSpaceList() {
        // given
        long userId = 1L;
        List<SpaceListResult> expectedResults = List.of(mock(SpaceListResult.class));

        given(spaceQueryRepository.findSpaceList(userId))
                .willReturn(expectedResults);

        // when
        List<SpaceListResult> actualResults = spaceListGetUseCase.execute(userId);

        // then
        assertThat(actualResults).isEqualTo(expectedResults);
        then(spaceQueryRepository)
                .should()
                .findSpaceList(userId);
    }
}
