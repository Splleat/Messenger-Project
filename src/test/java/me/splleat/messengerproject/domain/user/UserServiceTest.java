package me.splleat.messengerproject.domain.user;

import me.splleat.messengerproject.domain.user.exception.UserEmailDuplicatedException;
import me.splleat.messengerproject.domain.user.exception.UserNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserRepository;
import me.splleat.messengerproject.support.fixture.UserFixture;
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
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("이미 존재하는 이메일로 사용자를 등록하면 UserEmailDuplicatedException이 발생한다.")
    void register_WhenExistsEmail_ThrowsException() {
        // given
        User user = UserFixture.defaultUser();

        given(userRepository.existsByEmail(user.getEmail()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.register(user))
                .isInstanceOf(UserEmailDuplicatedException.class);

        then(userRepository)
                .should(never())
                .save(any(User.class));
    }

    @Test
    @DisplayName("올바른 이메일로 사용자를 조회하면 사용자 정보를 반환한다.")
    void getUser_WhenValidEmail_ReturnsUser() {
        // given
        User user = UserFixture.defaultUser();

        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(user));

        // when
        User found = userService.getUser(user.getEmail());

        // then
        then(userRepository)
                .should()
                .findByEmail(user.getEmail());

        assertThat(found)
                .isEqualTo(user);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자를 조회하면 UserNotFoundException이 발생한다.")
    void getUser_WhenNotExists_ThrowsException() {
        // given
        String email = "test@test.com";

        given(userRepository.findByEmail(email))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(email))
                .isInstanceOf(UserNotFoundException.class);
    }
}