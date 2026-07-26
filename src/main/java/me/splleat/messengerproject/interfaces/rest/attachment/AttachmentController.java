package me.splleat.messengerproject.interfaces.rest.attachment;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.storage.S3Service;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignRequest;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController implements AttachmentApi {
    private final S3Service s3Service;

    @Override
    @PostMapping("/presign")
    public PresignResponse presign(@RequestBody PresignRequest request) {
        return s3Service.createPresignedUpload(request);
    }
}
