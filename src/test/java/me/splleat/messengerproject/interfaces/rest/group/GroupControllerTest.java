package me.splleat.messengerproject.interfaces.rest.group;

import me.splleat.messengerproject.application.channel.GroupChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.GroupChannelEnterUseCase;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.group.*;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupCreateRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupInviteRequest;
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
import me.splleat.messengerproject.application.group.dto.GroupListResult;
import me.splleat.messengerproject.application.group.dto.GroupResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
@Import({TestSecurityConfig.class, JacksonConfig.class})
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupCreateUseCase groupCreateUseCase;

    @MockitoBean
    private GroupListGetUseCase groupListGetUseCase;

    @MockitoBean
    private GroupGetUseCase groupGetUseCase;

    @MockitoBean
    private GroupInviteUseCase groupInviteUseCase;

    @MockitoBean
    private GroupChannelCreateUseCase groupChannelCreateUseCase;

    @MockitoBean
    private GroupLeaveUseCase groupLeaveUseCase;

    @MockitoBean
    private GroupChannelEnterUseCase groupChannelEnterUseCase;

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 생성 요청을 보내면, 201 Created를 반환한다.")
    void createGroup_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        GroupCreateRequest request = new GroupCreateRequest("testGroup");

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
        GroupCreateRequest request = new GroupCreateRequest("");

        // when
        ResultActions result = mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("인증된 사용자가 그룹 목록을 요청하면 200 OK를 반환한다.")
    void getGroupList_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        List<GroupListResult> expected = List.of(new GroupListResult(1L, "testGroup"));

        given(groupListGetUseCase.execute(anyLong()))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/groups")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupId").isString())
                .andExpect(jsonPath("$[0].groupName").isString());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 초대 요청을 보내면, 201 Created를 반환한다.")
    void inviteGroup_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        GroupInviteRequest request = new GroupInviteRequest(List.of(1L, 2L));

        // when
        ResultActions result = mockMvc.perform(post("/groups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("그룹 상세 정보를 요청하면 200 OK를 반환한다.")
    void getGroup_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        GroupResult expected = new GroupResult(1L, "testGroup", List.of());

        given(groupGetUseCase.execute(anyLong(), anyLong()))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/groups/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.groupId").isString())
                .andExpect(jsonPath("$.groupName").isString())
                .andExpect(jsonPath("$.channelList").isArray());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("그룹 나가기를 요청하면 204 No Content를 반환한다.")
    void leaveGroup_WhenAuthenticated_ReturnsNoContent() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/groups/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 그룹 채널 생성 요청을 보내면, 201 Created를 반환한다.")
    void createGroupChannel_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        GroupChannelCreateRequest request = new GroupChannelCreateRequest("testChannel", ChannelType.TEXT);

        // when
        ResultActions result = mockMvc.perform(post("/groups/1/channels")
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
        GroupChannelCreateRequest request = new GroupChannelCreateRequest("", null);

        // when
        ResultActions result = mockMvc.perform(post("/groups/1/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("그룹 채널 입장을 요청하면 200 OK를 반환한다.")
    void enterGroupChannel_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        ChannelEnterResult expected = new ChannelEnterResult(List.of(), false, false, 0L, 0L, 0L);

        given(groupChannelEnterUseCase.execute(anyLong(), anyLong(), anyLong()))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/groups/1/channels/1")
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
