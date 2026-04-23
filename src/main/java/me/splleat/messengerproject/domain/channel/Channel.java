package me.splleat.messengerproject.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.group.Group;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChannelType type;


    @Builder
    private Channel(Group group, String name, ChannelType type) {
        this.group = group;
        this.name = name;
        this.type = type;
    }

    public static Channel create(Group group, String name, ChannelType type) {
        return Channel.builder()
                .group(group)
                .name(name)
                .type(type)
                .build();
    }

    public boolean isGroupChannel() {
        return group != null;
    }

    public Long getGroupId() {
        return group.getId();
    }
}
