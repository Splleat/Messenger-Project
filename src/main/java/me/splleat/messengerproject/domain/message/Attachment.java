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

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AttachmentType type;

    @Column(name = "url")
    private String url;

    public static Attachment create(long messageId, AttachmentType type, String url) {
        return new Attachment(messageId, type, url);
    }
}
