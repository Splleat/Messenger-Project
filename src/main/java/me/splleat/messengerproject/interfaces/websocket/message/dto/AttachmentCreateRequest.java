package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.domain.message.Attachment;
import me.splleat.messengerproject.domain.message.AttachmentType;

public record AttachmentCreateRequest(
        String type,
        String url
) {
    public Attachment toEntity(long messageId) {
        return Attachment.create(messageId, AttachmentType.from(type), url);
    }
}
