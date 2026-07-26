package me.splleat.messengerproject.interfaces.rest.profile;

import me.splleat.messengerproject.application.profile.*;
import me.splleat.messengerproject.application.profile.dto.*;
import me.splleat.messengerproject.common.config.JacksonConfig;
import me.splleat.messengerproject.interfaces.rest.profile.dto.UserProfileImageUpdateRequest;
import me.splleat.messengerproject.interfaces.rest.profile.dto.UserProfileUpdateRequest;
import me.splleat.messengerproject.support.TestSecurityConfig;
import me.splleat.messengerproject.support.annotation.WithMockPrincipal;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@Import({JacksonConfig.class, TestSecurityConfig.class})
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MyProfileGetUseCase myProfileGetUseCase;

    @MockitoBean
    private TargetProfileGetUseCase targetProfileGetUseCase;

    @MockitoBean
    private UserProfileUpdateUseCase userProfileUpdateUseCase;

    @MockitoBean
    private UserProfileImageUpdateUseCase userProfileImageUpdateUseCase;

    @MockitoBean
    private UserSearchUseCase userSearchUseCase;

    @Test
    @WithMockPrincipal
    @DisplayName("내 프로필 조회 요청 시, 200 OK와 프로필 상세 정보를 반환한다.")
    void getMyProfile_ReturnsOk() throws Exception {
        // given
        UserProfileDetailResult expected = mock(UserProfileDetailResult.class);
        given(myProfileGetUseCase.execute(any(Long.class)))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/profiles/me"));

        // then
        result.andExpect(status().isOk());
        then(myProfileGetUseCase)
                .should()
                .execute(any(Long.class));
    }

    @Test
    @WithMockPrincipal
    @DisplayName("타겟 프로필 조회 요청 시, 200 OK와 프로필 정보를 반환한다.")
    void getTargetProfile_ReturnsOk() throws Exception {
        // given
        long targetId = 1L;
        UserProfileResult expected = mock(UserProfileResult.class);
        given(targetProfileGetUseCase.execute(targetId))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/profiles/{id}", targetId));

        // then
        result.andExpect(status().isOk());
        then(targetProfileGetUseCase)
                .should()
                .execute(targetId);
    }

    @Test
    @WithMockPrincipal
    @DisplayName("유저 검색 요청 시, 200 OK와 검색 결과를 반환한다.")
    void searchUserProfiles_ReturnsOk() throws Exception {
        // given
        String name = "test";
        int page = 0;
        List<UserProfileResult> expected = List.of(mock(UserProfileResult.class));
        given(userSearchUseCase.searchOtherUserProfiles(any(UserSearchCommand.class)))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(get("/profiles/search")
                .param("name", name)
                .param("page", String.valueOf(page)));

        // then
        result.andExpect(status().isOk());
        then(userSearchUseCase)
                .should()
                .searchOtherUserProfiles(any(UserSearchCommand.class));
    }

    @Test
    @WithMockPrincipal
    @DisplayName("프로필 수정 요청 시, 204 No Content를 반환한다.")
    void updateProfile_ReturnsNoContent() throws Exception {
        // given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("newName", "newBio");

        // when
        ResultActions result = mockMvc.perform(put("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent());
        then(userProfileUpdateUseCase)
                .should()
                .execute(any(UserProfileUpdateCommand.class));
    }

    @Test
    @WithMockPrincipal
    @DisplayName("프로필 이미지 수정 요청 시, 204 No Content를 반환한다.")
    void updateProfileImage_ReturnsNoContent() throws Exception {
        // given
        UserProfileImageUpdateRequest request = new UserProfileImageUpdateRequest("new-image-key");

        // when
        ResultActions result = mockMvc.perform(patch("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent());
        then(userProfileImageUpdateUseCase)
                .should()
                .execute(any(UserProfileImageUpdateCommand.class));
    }
}
