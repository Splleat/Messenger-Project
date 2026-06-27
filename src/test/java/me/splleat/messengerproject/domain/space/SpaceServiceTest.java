package me.splleat.messengerproject.domain.space;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.SpaceRepository;
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
class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private SpaceService spaceService;

    @Test
    @DisplayName("그룹을 등록하면, 등록된 그룹의 정보가 반환된다.")
    void register_WhenValid_ReturnsSpace() {
        // given
        Space space = Space.create("test");

        given(spaceRepository.save(space))
                .willReturn(space);

        // when
        Space found = spaceService.register(space);

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(space);
    }
    
    @Test
    @DisplayName("요청 그룹 ID 목록이 비어 있으면, 빈 리스트가 반환된다.")
    void getSpaces_WhenEmptyList_ReturnsEmptyList() {
        // given
        List<Long> spaceIds = List.of();

        // when
        List<Space> found = spaceService.getSpaces(spaceIds);

        // then
        assertThat(found)
                .isEmpty();
    }

    @Test
    @DisplayName("요청 그룹 ID 목록이 주어지면, 해당하는 그룹 목록을 반환한다.")
    void getSpaces_WhenValidIds_ReturnsSpaces() {
        // given
        List<Long> spaceIds = List.of(1L, 2L);
        List<Space> spaces = List.of(Space.create("space1"), Space.create("space2"));

        given(spaceRepository.findAllByIdIn(spaceIds))
                .willReturn(spaces);

        // when
        List<Space> result = spaceService.getSpaces(spaceIds);

        // then
        assertThat(result)
                .hasSize(2)
                .isEqualTo(spaces);
    }

    @Test
    @DisplayName("그룹 ID로 그룹을 조회하면, 해당 그룹이 반환된다.")
    void getSpace_WhenExists_ReturnsSpace() {
        // given
        long spaceId = 1L;
        Space space = Space.create("test");

        given(spaceRepository.findById(spaceId))
                .willReturn(Optional.of(space));

        // when
        Space found = spaceService.getSpace(spaceId);

        // then
        assertThat(found)
                .isEqualTo(space);
    }

    @Test
    @DisplayName("존재하지 않는 그룹을 요청하려 하면, SpaceNotFoundException이 발생한다.")
    void getSpace_WhenNotExists_ThrowsException() {
        // given
        long spaceId = 1L;
        
        given(spaceRepository.findById(spaceId))
                .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> spaceService.getSpace(spaceId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SPACE_NOT_FOUND);
    }
}