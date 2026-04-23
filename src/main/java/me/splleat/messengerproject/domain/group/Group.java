package me.splleat.messengerproject.domain.group;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends SoftDeletableEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @Builder
    private Group(String name) {
        this.name = name;
    }

    public static Group create(String name) {
        return new Group(name);
    }
}
