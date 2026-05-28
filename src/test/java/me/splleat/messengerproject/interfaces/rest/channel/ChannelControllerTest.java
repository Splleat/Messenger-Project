package me.splleat.messengerproject.interfaces.rest.channel;

import me.splleat.messengerproject.application.channel.*;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.application.channel.dto.ChannelMessagePageResult;
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantResult;
import me.splleat.messengerproject.common.config.JacksonConfig;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelInviteRequest;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import({JacksonConfig.class, TestSecurityConfig.class})
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DirectChannelLeaveUseCase directChannelLeaveUseCase;

    @MockitoBean
    private DirectChannelInviteUseCase directChannelInviteUseCase;

    @MockitoBean
    private DirectChannelEnterUseCase directChannelEnterUseCase;

    @MockitoBean
    private ChannelParticipantGetUseCase channelParticipantGetUseCase;

    @MockitoBean
    private ChannelMessageCursorGetUseCase channelMessageCursorGetUseCase;

    @MockitoBean
    private ChannelListGetUseCase channelListGetUseCase;

    @MockitoBean
    private DirectChannelCreateUseCase directChannelCreateUseCase;

    @MockitoBean
    private ChannelReadMessageUpdateUseCase channelReadMessageUpdateUseCase;

    @Test
    @WithMockPrincipal
    @DisplayName("채널 목록을 요청하면 200 OK를 반환한다.")
    void getDirectChannels_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        List<ChannelListResult> expected = List.of(new ChannelListResult(1L, "testChannel", false));
        
        given(channelListGetUseCase.execute(any(Long.class)))
                .willReturn(expected);
                
        // when
        ResultActions result = mockMvc.perform(get("/channels")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channelId").isString())
                .andExpect(jsonPath("$[0].channelName").isString())
                .andExpect(jsonPath("$[0].hasUnread").isBoolean());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 다이렉트 채널 생성 요청을 보내면, 201 Created를 반환한다.")
    void createDirectChannel_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        DirectChannelCreateRequest request = new DirectChannelCreateRequest("testChanel", ChannelType.TEXT);

        // when
        ResultActions result = mockMvc.perform(post("/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 다이렉트 채널 초대 요청을 보내면, 201 Created를 반환한다.")
    void inviteDirectChannel_WhenValidRequest_ReturnsCreated() throws Exception {
        // given
        DirectChannelInviteRequest request = new DirectChannelInviteRequest(List.of(1L, 2L, 3L));

        // when
        ResultActions result = mockMvc.perform(post("/channels/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("초대할 멤버 목록이 비어있으면 400 Bad Request를 반환한다.")
    void inviteDirectChannel_WhenEmptyMembers_ReturnsBadRequest() throws Exception {
        // given
        DirectChannelInviteRequest request = new DirectChannelInviteRequest(List.of());

        // when
        ResultActions result = mockMvc.perform(post("/channels/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("채널 나가기를 요청하면 204 No Content를 반환한다.")
    void leaveChannel_WhenAuthenticated_ReturnsNoContent() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/channels/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("채널 참여자 목록을 요청하면 200 OK를 반환한다.")
    void getChannelParticipants_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        List<ChannelParticipantResult> expected = List.of(new ChannelParticipantResult(1L, "testUser", "profileImage", "hello"));
        
        given(channelParticipantGetUseCase.execute(any(Long.class), any(Long.class)))
                .willReturn(expected);
        
        // when
        ResultActions result = mockMvc.perform(get("/channels/1/participants")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").isString())
                .andExpect(jsonPath("$[0].username").isString())
                .andExpect(jsonPath("$[0].profileImage").isString())
                .andExpect(jsonPath("$[0].statusMessage").isString());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("채널 입장을 요청하면 200 OK를 반환한다.")
    void channelEnter_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        ChannelEnterResult expected = new ChannelEnterResult(List.of(), false, false, 0L, 0L, 0L);

        given(directChannelEnterUseCase.execute(any(Long.class), any(Long.class)))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/channels/1")
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

    @Test
    @WithMockPrincipal
    @DisplayName("커서 기반 채널 메시지 조회를 요청하면 200 OK를 반환한다.")
    void getChannelMessagesByCursor_WhenAuthenticated_ReturnsOk() throws Exception {
        // given
        ChannelMessagePageResult expected = new ChannelMessagePageResult(List.of(), false, 0L);
        
        given(channelMessageCursorGetUseCase.execute(any()))
                .willReturn(expected);
        
        // when
        ResultActions result = mockMvc.perform(get("/channels/1/messages")
                .param("cursorId", "0")
                .param("direction", "NEXT")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.hasMore").isBoolean())
                .andExpect(jsonPath("$.cursorId").isString());
    }

    @Test
    @WithMockPrincipal
    @DisplayName("마지막으로 읽은 메시지 갱신을 요청하면 204 No Content를 반환한다.")
    void updateLastRead_WhenValidRequest_ReturnsNoContent() throws Exception {
        // given
        String request = "{\"lastReadMessageId\": 1}";

        // when
        ResultActions result = mockMvc.perform(patch("/channels/1/read")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        result.andExpect(status().isNoContent());
    }
}
