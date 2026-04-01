package me.splleat.messengerproject.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.splleat.messengerproject.domain.user.exception.UserPasswordMismatchException;
import me.splleat.messengerproject.infrastructure.persistence.entity.SoftDeletableEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeletableEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Getter
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    private User(String email, String passwordHash, UserRole role, UserStatus status, LocalDateTime lastLoginAt) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
    }

    public static User create(String email, String passwordHash, UserRole role) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public void login(String rawPassword, PasswordEncoder encoder) {
        if (!encoder.matches(rawPassword, this.passwordHash)) {
            throw new UserPasswordMismatchException();
        }

        this.lastLoginAt = LocalDateTime.now();
    }
}
