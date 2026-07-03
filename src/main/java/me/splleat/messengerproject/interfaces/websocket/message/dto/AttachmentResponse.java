package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.domain.message.Attachment;
import me.splleat.messengerproject.domain.message.AttachmentType;

public record AttachmentResponse(
        long id,
        long messageId,
        String name,
        AttachmentType type,
        String url,
        long size
) {
    public static AttachmentResponse of(Attachment attachment, String absoluteUrl) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getMessageId(),
                attachment.getOriginalName(),
                attachment.getType(),
                absoluteUrl,
                attachment.getSize()
        );
    }
}
