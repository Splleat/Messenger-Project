package me.splleat.messengerproject.interfaces.rest;

import me.splleat.messengerproject.application.auth.LoginUseCase;
import me.splleat.messengerproject.application.result.LoginResult;
import me.splleat.messengerproject.interfaces.rest.request.LoginRequest;
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

    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인을 시도하면 200 OK를 반환한다.")
    void login_WhenValidCredential_ReturnOk() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        LoginResult result = new LoginResult("accessToken", "refreshToken", "test", "profileImage", "statusMessage");

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
    void login_WhenEmptyCredential_ReturnConflict() throws Exception {
        // given
        LoginRequest request = new LoginRequest(null, "");

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest());
    }
}