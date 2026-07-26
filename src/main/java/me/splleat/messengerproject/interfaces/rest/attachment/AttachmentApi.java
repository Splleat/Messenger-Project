package me.splleat.messengerproject.interfaces.rest.attachment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignRequest;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Attachment", description = "첨부파일 업로드 관련 API")
public interface AttachmentApi {

    @Operation(summary = "첨부파일 업로드 Presigned URL 발급", description = "S3에 직접 업로드할 수 있는 Presigned URL을 발급한다.")
    @ApiResponse(responseCode = "200", description = "발급 성공")
    PresignResponse presign(@RequestBody PresignRequest request);
}
