package me.splleat.messengerproject.domain.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Attachment extends SoftDeletableEntity {

    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "original_name")
    private String originalName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AttachmentType type;

    @Column(name = "url")
    private String url;

    @Column(name = "size")
    private Long size;

    public static Attachment create(long messageId, String originalName, AttachmentType type, String url, long size) {
        return new Attachment(messageId, originalName, type, url, size);
    }
}
