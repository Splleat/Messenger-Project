package me.splleat.messengerproject.interfaces.websocket.message.dto;

import me.splleat.messengerproject.domain.message.Attachment;
import me.splleat.messengerproject.domain.message.AttachmentType;

public record AttachmentCreateRequest(
        String name,
        String type,
        String url,
        long size
) {
    public Attachment toEntity(long messageId) {
        return Attachment.create(messageId, name, AttachmentType.from(type), url, size);
    }
}
