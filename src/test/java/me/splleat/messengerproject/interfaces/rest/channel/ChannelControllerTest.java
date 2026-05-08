package me.splleat.messengerproject.interfaces.rest.channel;

import me.splleat.messengerproject.application.channel.ChannelListGetUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelInviteUseCase;
import me.splleat.messengerproject.application.channel.GroupChannelCreateUseCase;
import me.splleat.messengerproject.domain.channel.ChannelType;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelInviteRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupChannelCreateRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import(TestSecurityConfig.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupChannelCreateUseCase groupChannelCreateUseCase;

    @MockitoBean
    private DirectChannelCreateUseCase directChannelCreateUseCase;

    @MockitoBean
    private DirectChannelInviteUseCase directChannelInviteUseCase;

    @MockitoBean
    private ChannelListGetUseCase channelListGetUseCase;

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
}
