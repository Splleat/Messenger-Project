package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.domain.message.Attachment;
import me.splleat.messengerproject.domain.message.AttachmentType;

public record AttachmentResponse(
        long id,
        long messageId,
        AttachmentType type,
        String url
) {
    public static AttachmentResponse of(Attachment attachment, String absoluteUrl) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getMessageId(),
                attachment.getType(),
                absoluteUrl
        );
    }
}
