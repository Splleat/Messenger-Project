package me.splleat.messengerproject.application.auth;

import me.splleat.messengerproject.domain.profile.UserProfile;
import me.splleat.messengerproject.domain.profile.UserProfileService;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.domain.user.UserService;
import me.splleat.messengerproject.interfaces.rest.request.SignUpRequest;
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
class SignUpUseCaseTest {
    @Mock
    private UserService mockUserService;

    @Mock
    private UserProfileService mockUserProfileService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SignUpUseCase signUpUseCase;

    @Test
    @DisplayName("올바른 회원가입 정보를 입력하면 회원가입이 성공한다.")
    void execute_WhenValidCommand_Success() {
        // given
        SignUpRequest request = new SignUpRequest("테스트", "test@test.com", "password1234");

        // when
        signUpUseCase.execute(request.toCommand());

        // then
        then(mockUserService)
                .should()
                .register(any(User.class));

        then(mockUserProfileService)
                .should()
                .register(any(UserProfile.class));
    }
}