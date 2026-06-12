package me.splleat.messengerproject.domain.space;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

@Entity
@Table(name = "spaces")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space extends SoftDeletableEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @Builder
    private Space(String name) {
        this.name = name;
    }

    public static Space create(String name) {
        return new Space(name);
    }
}
