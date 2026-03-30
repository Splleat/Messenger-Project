package me.splleat.messengerproject.domain.profile;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.user.User;

@Entity
@Table(name = "user_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status_message")
    private String statusMessage;

    @Builder
    private UserProfile(User user) {
        this.user = user;
    }

    public static UserProfile create(User user) {
        return UserProfile.builder()
                .user(user)
                .build();
    }
}
