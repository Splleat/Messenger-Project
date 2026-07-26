package me.splleat.messengerproject.application.profile;

import me.splleat.messengerproject.application.profile.dto.UserProfileDetailResult;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.UserProfileQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MyProfileGetUseCaseTest {

    @Mock
    private UserProfileQueryRepository userProfileQueryRepository;

    @InjectMocks
    private MyProfileGetUseCase myProfileGetUseCase;

    @Test
    @DisplayName("자신의 프로필 조회 요청 시, 프로필 정보를 반환한다.")
    void execute_WhenProfileExists_ReturnsProfileDetail() {
        // given
        long userId = 1L;
        UserProfileDetailResult expectedResult = mock(UserProfileDetailResult.class);

        given(userProfileQueryRepository.getMyProfile(userId))
                .willReturn(Optional.of(expectedResult));

        // when
        UserProfileDetailResult actualResult = myProfileGetUseCase.execute(userId);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("자신의 프로필이 존재하지 않으면, 예외가 발생한다.")
    void execute_WhenProfileNotFound_ThrowsException() {
        // given
        long userId = 1L;

        given(userProfileQueryRepository.getMyProfile(userId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> myProfileGetUseCase.execute(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PROFILE_NOT_FOUND);
    }
}
