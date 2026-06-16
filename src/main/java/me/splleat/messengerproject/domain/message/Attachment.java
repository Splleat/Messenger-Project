package me.splleat.messengerproject.domain.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends SoftDeletableEntity {

    @Column(name = "message_id")
    private Long messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AttachmentType type;

    @Column(name = "url")
    private String url;
}
