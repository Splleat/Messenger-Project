package me.splleat.messengerproject.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "user_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseEntity {

    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Getter
    @Column(name = "name")
    private String name;

    @Getter
    @Column(name = "image_url")
    private String imageUrl;

    @Getter
    @Column(name = "status_message")
    private String statusMessage;

    @Builder
    private UserProfile(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static UserProfile create(Long userId, String name) {
        return UserProfile.builder()
                .userId(userId)
                .name(name)
                .build();
    }
}
