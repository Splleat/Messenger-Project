package me.splleat.messengerproject.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    @Getter
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Getter
    @Column(name = "is_admin")
    private boolean isAdmin;

    @Getter
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    private User(String email, String passwordHash, boolean isAdmin, LocalDateTime lastLoginAt) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
        this.lastLoginAt = lastLoginAt;
    }

    public static User create(String email, String passwordHash, boolean isAdmin) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .isAdmin(isAdmin)
                .build();
    }

    public void login(String rawPassword, PasswordEncoder encoder) {
        if (!encoder.matches(rawPassword, this.passwordHash)) {
            throw new UserPasswordMismatchException();
        }

        this.lastLoginAt = LocalDateTime.now();
    }
}
