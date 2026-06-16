package me.splleat.messengerproject.interfaces.rest.attachment;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.message.AttachmentService;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignRequest;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/presign")
    public PresignResponse presign(@RequestBody PresignRequest request) {
        return attachmentService.createPresignedUpload(request);
    }
}
