package me.splleat.messengerproject.domain.user;

import me.splleat.messengerproject.domain.user.exception.UserNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.UserRepository;
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
class UserServiceTest {
    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("올바른 이메일로 사용자를 조회하면 사용자 정보를 반환한다.")
    void getUser_WhenValidEmail_ReturnUser() {
        String email = "test@test.com";
        User user = mock(User.class);

        // given
        given(mockUserRepository.findByEmail(email))
                .willReturn(Optional.of(user));

        // when
        User found = userService.getUser(email);

        // then
        then(mockUserRepository)
                .should()
                .findByEmail(email);

        assertThat(found)
                .isEqualTo(user);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자를 조회하면 UserNotFoundException이 발생한다.")
    void getUser_WhenNotExists_ThrowsException() {
        // given
        String email = "test@test.com";

        given(mockUserRepository.findByEmail(email))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(email))
                .isInstanceOf(UserNotFoundException.class);
    }
}