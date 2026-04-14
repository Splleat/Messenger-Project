package me.splleat.messengerproject.domain.profile;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.user.User;
import me.splleat.messengerproject.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "user_profiles")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseEntity {

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status_message")
    private String statusMessage;

    @Builder
    private UserProfile(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public static UserProfile create(User user, String name) {
        return UserProfile.builder()
                .user(user)
                .name(name)
                .build();
    }
}
