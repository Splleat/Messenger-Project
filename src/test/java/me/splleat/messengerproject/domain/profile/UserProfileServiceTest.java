package me.splleat.messengerproject.domain.profile;

import me.splleat.messengerproject.domain.profile.exception.UserProfileNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    @Mock
    private UserProfileRepository mockUserProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    @DisplayName("올바른 사용자 아이디로 프로필을 조회하면 프로필 정보를 반환한다.")
    void getUserProfile_WhenValidUserId_ReturnUserProfile() {
        // given
        Long userId = 1L;
        UserProfile profile = mock(UserProfile.class);

        given(mockUserProfileRepository.findById(userId))
                .willReturn(Optional.of(profile));

        // when
        UserProfile found = userProfileService.getUserProfile(userId);

        // then
        then(mockUserProfileRepository)
                .should()
                .findById(userId);

        assertThat(found)
                .isEqualTo(profile);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 아이디로 프로필을 조회하면 UserProfileNotFoundException이 발생한다.")
    void getUserProfile_WhenNotExists_ThrowsException() {
        // given
        Long userId = 1L;

        given(mockUserProfileRepository.findById(userId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userProfileService.getUserProfile(userId))
                .isInstanceOf(UserProfileNotFoundException.class);
    }
}