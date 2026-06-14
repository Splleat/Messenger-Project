package me.splleat.messengerproject.interfaces.rest.space;

import me.splleat.messengerproject.application.channel.SpaceChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.SpaceChannelEnterUseCase;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.space.*;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.interfaces.rest.channel.dto.SpaceChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.space.dto.SpaceCreateRequest;
import me.splleat.messengerproject.interfaces.rest.space.dto.SpaceInviteRequest;
import me.splleat.messengerproject.common.config.JacksonConfig;
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
import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.application.space.dto.SpaceResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpaceController.class)
@Import({TestSecurityConfig.class, JacksonConfig.class})
class SpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpaceCreateUseCase spaceCreateUseCase;

    @MockitoBean
    private SpaceListGetUseCase spaceListGetUseCase;

    @MockitoBean
    private SpaceGetUseCase spaceGetUseCase;

    @MockitoBean
    private SpaceInviteUseCase spaceInviteUseCase;

    @MockitoBean
    private SpaceChannelCreateUseCase spaceChannelCreateUseCase;

    @MockitoBean
    private SpaceLeaveUseCase spaceLeaveUseCase;

    @MockitoBean
    private SpaceChannelEnterUseCase spaceChannelEnterUseCase;

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 생성 요청을 보내면, 201 Created를 반환한다.")
    void createSpace_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        SpaceCreateRequest request = new SpaceCreateRequest("testSpace");

        // when
        ResultActions result = mockMvc.perform(post("/spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("요청 정보가 null이거나 빈 값이면 400 Bad Request를 반환한다.")
    void createSpace_WhenEmptyRequest_ReturnsBadRequest() throws Exception {
        // given
        SpaceCreateRequest request = new SpaceCreateRequest("");

        // when
        ResultActions result = mockMvc.perform(post("/spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("인증된 사용자가 그룹 목록을 요청하면 200 OK를 반환한다.")
    void getSpaceList_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        List<SpaceListResult> expected = List.of(new SpaceListResult(1L, "testSpace"));

        given(spaceListGetUseCase.execute(anyLong()))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/spaces")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].spaceId").isString())
                .andExpect(jsonPath("$[0].spaceName").isString());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 초대 요청을 보내면, 201 Created를 반환한다.")
    void inviteSpace_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        SpaceInviteRequest request = new SpaceInviteRequest(List.of(1L, 2L));

        // when
        ResultActions result = mockMvc.perform(post("/spaces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("그룹 상세 정보를 요청하면 200 OK를 반환한다.")
    void getSpace_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        SpaceResult expected = new SpaceResult(1L, "testSpace", List.of());

        given(spaceGetUseCase.execute(anyLong(), anyLong()))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/spaces/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").isString())
                .andExpect(jsonPath("$.spaceName").isString())
                .andExpect(jsonPath("$.channelList").isArray());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("그룹 나가기를 요청하면 204 No Content를 반환한다.")
    void leaveSpace_WhenAuthenticated_ReturnsNoContent() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/spaces/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 채널 생성 요청을 보내면, 201 Created를 반환한다.")
    void createSpaceChannel_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        SpaceChannelCreateRequest request = new SpaceChannelCreateRequest("testChannel", ChannelType.TEXT);

        // when
        ResultActions result = mockMvc.perform(post("/spaces/1/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("채널 생성 요청 정보가 누락되면 400 Bad Request를 반환한다.")
    void createChannel_WhenInvalidRequest_ReturnsBadRequest() throws Exception {
        // given
        SpaceChannelCreateRequest request = new SpaceChannelCreateRequest("", null);

        // when
        ResultActions result = mockMvc.perform(post("/spaces/1/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("그룹 채널 입장을 요청하면 200 OK를 반환한다.")
    void enterSpaceChannel_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        ChannelEnterResult expected = new ChannelEnterResult(List.of(), false, false, 0L, 0L, 0L);

        given(spaceChannelEnterUseCase.execute(anyLong(), anyLong(), anyLong()))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/spaces/1/channels/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.hasPrev").isBoolean())
                .andExpect(jsonPath("$.hasNext").isBoolean())
                .andExpect(jsonPath("$.prevCursorId").isString())
                .andExpect(jsonPath("$.nextCursorId").isString())
                .andExpect(jsonPath("$.lastReadMessageId").isString());
    }
}
