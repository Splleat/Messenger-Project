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

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChannelType type;

    @Builder
    private Channel(Long groupId, String name, ChannelType type) {
        this.groupId = groupId;
        this.name = name;
        this.type = type;
    }

    public static Channel createDirectChannel(String name, ChannelType type) {
        return Channel.builder()
                .name(name)
                .type(type)
                .build();
    }

    public static Channel createGroupChannel(long groupId, String name, ChannelType type) {
        return Channel.builder()
                .groupId(groupId)
                .name(name)
                .type(type)
                .build();
    }

    public boolean isGroupChannel() {
        return groupId != null;
    }
}
