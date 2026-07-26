package me.splleat.messengerproject.application.profile;

import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.user.UserProfile;
import me.splleat.messengerproject.domain.user.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TargetProfileGetUseCaseTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private StorageUrlMapper storageUrlMapper;

    @InjectMocks
    private TargetProfileGetUseCase targetProfileGetUseCase;

    @Test
    @DisplayName("상대방 프로필 조회 요청 시, 프로필 정보를 반환한다.")
    void execute_ReturnsUserProfileResult() {
        // given
        long userId = 1L;
        String rawImageUrl = "profile.png";
        String resolvedImageUrl = "https://storage.com/profile.png";
        UserProfile profile = mock(UserProfile.class);

        given(userProfileService.getUserProfile(userId))
                .willReturn(profile);
        given(profile.getImageUrl())
                .willReturn(rawImageUrl);
        given(storageUrlMapper.resolve(rawImageUrl))
                .willReturn(resolvedImageUrl);

        // when
        UserProfileResult result = targetProfileGetUseCase.execute(userId);

        // then
        assertThat(result.imageUrl())
                .isEqualTo(resolvedImageUrl);
    }
}
