package me.splleat.messengerproject.interfaces.rest;

import me.splleat.messengerproject.application.auth.LoginUseCase;
import me.splleat.messengerproject.application.auth.LogoutUseCase;
import me.splleat.messengerproject.application.auth.RegisterUseCase;
import me.splleat.messengerproject.application.auth.result.LoginResult;
import me.splleat.messengerproject.interfaces.rest.auth.AuthController;
import me.splleat.messengerproject.interfaces.rest.auth.request.LoginRequest;
import me.splleat.messengerproject.interfaces.rest.auth.request.LogoutRequest;
import me.splleat.messengerproject.interfaces.rest.auth.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RegisterUseCase registerUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인을 시도하면 200 OK를 반환한다.")
    void login_WhenValidCredential_ReturnsOk() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        LoginResult value = new LoginResult("accessToken", "refreshToken", 1L, "test", "profileImage", "statusMessage");

        given(loginUseCase.execute(request.toCommand()))
                .willReturn(value);

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(value.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(value.refreshToken()));
    }

    @Test
    @DisplayName("요청 이메일과 비밀번호가 null이거나 빈 값이면 400 Bad Request를 반환한다.")
    void login_WhenEmptyCredential_ReturnsBadRequest() throws Exception {
        // given
        LoginRequest request = new LoginRequest(null, "");

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("올바른 사용자 정보로 회원가입을 시도하면 201 Created를 반환한다.")
    void register_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("테스트", "test@test.com", "password1234");

        // when
        ResultActions result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("요청 사용자 정보가 null이거나 빈 값이면 400 Bad Request를 반환한다.")
    void register_WhenEmptyRequest_ReturnsBadRequest() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("", null, "");

        // when
        ResultActions result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400 Bad Request를 반환한다.")
    void register_WhenInvalidEmail_ReturnsBadRequest() throws Exception{
        // given
        RegisterRequest request = new RegisterRequest("테스트", "test", "password123");

        // when
        ResultActions result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 400 Bad Request를 반환한다.")
    void register_WhenShortPassword_ReturnsBadRequest() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("테스트", "test@test.com", "pw");

        // when
        ResultActions result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("올바른 액세스 토큰과 리프레시 토큰이라면, 200 OK를 반환한다.")
    void logout_WhenValidTokens_ReturnsOk() throws Exception{
        // given
        LogoutRequest request = new LogoutRequest("accessToken", "refreshToken");

        // when
        ResultActions result = mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());
    }
}