package me.splleat.messengerproject.interfaces.rest.attachment;

import me.splleat.messengerproject.domain.message.AttachmentService;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignRequest;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignResponse;
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
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachmentController.class)
@Import({JacksonConfig.class, TestSecurityConfig.class})
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttachmentService attachmentService;

    @Test
    @WithMockPrincipal
    @DisplayName("올바른 형식의 Presign 요청을 보내면, 200 OK와 Presigned URL을 반환한다.")
    void presign_WhenValidRequest_ReturnsOk() throws Exception {
        // given
        PresignRequest request = new PresignRequest("test.png", "image/png", 1024L);
        PresignResponse expected = new PresignResponse("http://localhost:9000/messenger/attachments/test-uuid", "attachments/test-uuid");

        given(attachmentService.createPresignedUpload(any(PresignRequest.class)))
                .willReturn(expected);

        // when
        ResultActions result = mockMvc.perform(post("/attachments/presign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value("http://localhost:9000/messenger/attachments/test-uuid"))
                .andExpect(jsonPath("$.objectKey").value("attachments/test-uuid"));
    }
}
