package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

import java.util.UUID;

@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends SoftDeletableEntity {
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChannelType type;

    @Column(name = "invite_code")
    private String inviteCode;

    @Builder
    private Channel(String name, ChannelType type, String inviteCode) {
        this.name = name;
        this.type = type;
        this.inviteCode = inviteCode;
    }

    public static Channel create(String name, ChannelType type) {
        return Channel.builder()
                .name(name)
                .type(type)
                .inviteCode(UUID.randomUUID().toString())
                .build();
    }
}
