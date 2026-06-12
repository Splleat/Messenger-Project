package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends SoftDeletableEntity {

    @Column(name = "space_id")
    private Long spaceId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChannelType type;

    @Builder
    private Channel(Long spaceId, String name, ChannelType type) {
        this.spaceId = spaceId;
        this.name = name;
        this.type = type;
    }

    public static Channel createDirectChannel(String name, ChannelType type) {
        return Channel.builder()
                .name(name)
                .type(type)
                .build();
    }

    public static Channel createSpaceChannel(long spaceId, String name, ChannelType type) {
        return Channel.builder()
                .spaceId(spaceId)
                .name(name)
                .type(type)
                .build();
    }

    public boolean isSpaceChannel() {
        return spaceId != null;
    }
}
