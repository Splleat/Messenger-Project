package me.splleat.messengerproject.interfaces.rest.group;

import me.splleat.messengerproject.application.group.GroupCreateUseCase;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupCreateRequest;
import me.splleat.messengerproject.support.TestSecurityConfig;
import me.splleat.messengerproject.support.WithMockPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
@Import(TestSecurityConfig.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupCreateUseCase groupCreateUseCase;

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 생성 요청을 보내면, 201 Created를 반환한다.")
    void createGroup_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        GroupCreateRequest request = new GroupCreateRequest("testGroup", "testName");

        // when
        ResultActions result = mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("요청 정보가 null이거나 빈 값이면 400 Bad Request를 반환한다.")
    void createGroup_WhenEmptyRequest_ReturnsBadRequest() throws Exception {
        // given
        GroupCreateRequest request = new GroupCreateRequest("", "");

        // when
        ResultActions result = mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }
}