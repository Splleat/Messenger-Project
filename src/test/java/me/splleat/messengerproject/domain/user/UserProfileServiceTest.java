package me.splleat.messengerproject.domain.user;

import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserProfileRepository;
import me.splleat.messengerproject.support.fixture.UserProfileFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    @DisplayName("이미 존재하는 사용자 아이디로 프로필을 등록하면 UserProfileAlreadyExistsException이 발생한다.")
    void register_WhenExistsUserId_ThrowsException() {
        // given
        UserProfile profile = UserProfileFixture.defaultUserProfile(1L);

        given(userProfileRepository.existsByUserId(profile.getUserId()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> userProfileService.register(profile))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PROFILE_ALREADY_EXISTS);

        then(userProfileRepository)
                .should(never())
                .save(any(UserProfile.class));
    }

    @Test
    @DisplayName("올바른 사용자 아이디로 프로필을 조회하면 프로필 정보를 반환한다.")
    void getUserProfile_WhenValidUserId_ReturnsUserProfile() {
        // given
        long userId = 1L;
        UserProfile profile = UserProfileFixture.defaultUserProfile(userId);

        given(userProfileRepository.findByUserId(userId))
                .willReturn(Optional.of(profile));

        // when
        UserProfile found = userProfileService.getUserProfile(profile.getUserId());

        // then
        then(userProfileRepository)
                .should()
                .findByUserId(profile.getUserId());

        assertThat(found)
                .isEqualTo(profile);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 아이디로 프로필을 조회하면 UserProfileNotFoundException이 발생한다.")
    void getUserProfile_WhenNotExists_ThrowsException() {
        // given
        Long userId = 1L;

        given(userProfileRepository.findByUserId(userId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userProfileService.getUserProfile(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PROFILE_NOT_FOUND);
    }
}
