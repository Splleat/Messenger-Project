package me.splleat.messengerproject.interfaces.rest;

import me.splleat.messengerproject.application.auth.LoginUseCase;
import me.splleat.messengerproject.application.auth.SignUpUseCase;
import me.splleat.messengerproject.application.result.LoginResult;
import me.splleat.messengerproject.interfaces.rest.request.LoginRequest;
import me.splleat.messengerproject.interfaces.rest.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginUseCase mockLoginUseCase;

    @MockitoBean
    private SignUpUseCase mockSignUpUseCase;

    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인을 시도하면 200 OK를 반환한다.")
    void login_WhenValidCredential_ReturnsOk() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        LoginResult result = new LoginResult("accessToken", "refreshToken", 1L, "test", "profileImage", "statusMessage");

        given(mockLoginUseCase.execute(request.toCommand()))
                .willReturn(result);

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(result.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(result.refreshToken()));
    }

    @Test
    @DisplayName("요청 이메일과 비밀번호가 null이거나 빈 값이면 400 Bad Request를 반환한다.")
    void login_WhenEmptyCredential_ReturnsBadRequest() throws Exception {
        // given
        LoginRequest request = new LoginRequest(null, "");

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("올바른 사용자 정보로 회원가입을 시도하면 201 Created를 반환한다.")
    void signUp_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("테스트", "test@test.com", "password1234");

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("요청 사용자 정보가 null이거나 빈 값이면 400 Bad Request를 반환한다.")
    void signUp_WhenEmptyRequest_ReturnsBadRequest() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("", null, "");

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400 Bad Request를 반환한다.")
    void signUp_WhenInvalidEmail_ReturnsBadRequest() throws Exception{
        // given
        SignUpRequest request = new SignUpRequest("테스트", "test", "password123");

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 400 Bad Request를 반환한다.")
    void signUp_WhenShortPassword_ReturnsBadRequest() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("테스트", "test@test.com", "pw");

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}