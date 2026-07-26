package me.splleat.messengerproject.application.profile;

import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.application.profile.dto.UserSearchCommand;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.UserProfileQueryRepository;
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
class UserSearchUseCaseTest {

    @Mock
    private UserProfileQueryRepository userProfileQueryRepository;

    @InjectMocks
    private UserSearchUseCase userSearchUseCase;

    @Test
    @DisplayName("사용자 검색 요청 시, 리포지토리를 통해 검색 결과를 반환한다.")
    void searchOtherUserProfiles_ReturnsSearchResults() {
        // given
        long excludeUserId = 1L;
        String name = "searchName";
        int page = 0;
        UserSearchCommand command = new UserSearchCommand(excludeUserId, name, page);
        List<UserProfileResult> expectedResults = List.of(mock(UserProfileResult.class));

        given(userProfileQueryRepository.searchOtherUserProfiles(excludeUserId, name, page))
                .willReturn(expectedResults);

        // when
        List<UserProfileResult> actualResults = userSearchUseCase.searchOtherUserProfiles(command);

        // then
        assertThat(actualResults)
                .isEqualTo(expectedResults);
        then(userProfileQueryRepository)
                .should()
                .searchOtherUserProfiles(excludeUserId, name, page);
    }
}
