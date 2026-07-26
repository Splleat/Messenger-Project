package me.splleat.messengerproject.application.profile;

import me.splleat.messengerproject.application.profile.dto.UserProfileUpdateCommand;
import me.splleat.messengerproject.domain.user.UserProfileService;
import me.splleat.messengerproject.infrastructure.cache.UserProfileCacheEvictor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserProfileUpdateUseCaseTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserProfileCacheEvictor cacheEvictor;

    @InjectMocks
    private UserProfileUpdateUseCase userProfileUpdateUseCase;

    @Test
    @DisplayName("프로필 수정 요청 시, 프로필 정보를 수정하고 관련 캐시를 삭제한다.")
    void execute_UpdatesProfileAndEvictsCache() {
        // given
        long userId = 1L;
        String name = "newName";
        String statusMessage = "newStatus";
        UserProfileUpdateCommand command = new UserProfileUpdateCommand(userId, name, statusMessage);

        // when
        userProfileUpdateUseCase.execute(command);

        // then
        then(userProfileService)
                .should()
                .updateProfile(userId, name, statusMessage);

        then(cacheEvictor)
                .should()
                .evictMyProfile(userId);

        then(cacheEvictor)
                .should()
                .evictUserProfile(userId);
    }
}
