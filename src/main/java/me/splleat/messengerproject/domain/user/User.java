package me.splleat.messengerproject.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeletableEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    private User(String name, String email, String passwordHash, UserRole role, UserStatus status, LocalDateTime lastLoginAt) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
    }

    public static User create(String name, String email, String passwordHash, UserRole role) {
        return User.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
