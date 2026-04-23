package me.splleat.messengerproject.domain.user;

import me.splleat.messengerproject.domain.user.exception.UserPasswordMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    static class UserFixture {
        static final String PASSWORD_HASH = "encryptedPassword";

        static User defaultUser() {
            return User.create(
                    "test@test.com",
                    PASSWORD_HASH,
                    false
            );
        }
    }

    @Test
    @DisplayName("올바른 비밀번호로 로그인을 시도하면 예외가 발생하지 않는다.")
    void login_WhenValidPassword_DoesNotThrows() {
        // given
        User user = UserFixture.defaultUser();
        String password = "validPassword";

        given(passwordEncoder.matches(password, UserFixture.PASSWORD_HASH))
                .willReturn(true);

        // when & then
        assertThatCode(() -> user.login(password, passwordEncoder))
                .doesNotThrowAnyException();

        assertThat(user.getLastLoginAt())
                .isNotNull();
    }

    @Test
    @DisplayName("일치하지 않는 비밀번호로 로그인을 시도하면 UserPasswordMismatchException이 발생한다.")
    void login_WhenInvalidPassword_ThrowsException() {
        // given
        User user = UserFixture.defaultUser();
        String wrongPassword = "wrongPassword";

        given(passwordEncoder.matches(wrongPassword, UserFixture.PASSWORD_HASH))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> user.login(wrongPassword, passwordEncoder))
                .isInstanceOf(UserPasswordMismatchException.class);
    }
}