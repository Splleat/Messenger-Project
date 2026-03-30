package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends SoftDeletableEntity {
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChannelType type;

    @Builder
    private Channel(String name, ChannelType type) {
        this.name = name;
        this.type = type;
    }

    public static Channel create(String name, ChannelType type) {
        return Channel.builder()
                .name(name)
                .type(type)
                .build();
    }
}
