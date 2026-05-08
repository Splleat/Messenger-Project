package me.splleat.messengerproject.infrastructure.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal implements Principal, UserDetails {

    private final Long userId;
    private final boolean isAdmin;

    public static UserPrincipal create(Long userId, boolean isAdmin) {
        return new UserPrincipal(userId, isAdmin);
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public @NonNull String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
