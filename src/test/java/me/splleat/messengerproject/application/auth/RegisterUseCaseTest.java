package me.splleat.messengerproject.application.auth;

import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.interfaces.rest.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {
    @Mock
    private UserService userService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUseCase registerUseCase;

    @Test
    @DisplayName("올바른 회원가입 정보를 입력하면 회원가입이 성공한다.")
    void execute_WhenValidCommand_Success() {
        // given
        RegisterRequest request = new RegisterRequest("테스트", "test@test.com", "password1234");

        // when
        registerUseCase.execute(request.toCommand());

        // then
        then(userService)
                .should()
                .register(any(User.class));

        then(userProfileService)
                .should()
                .register(any(UserProfile.class));

        then(passwordEncoder)
                .should()
                .encode(request.password());
    }
}