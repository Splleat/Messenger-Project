package me.splleat.messengerproject.support;

import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.support.annotation.WithMockPrincipal;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockUserSecurityFactory implements WithSecurityContextFactory<WithMockPrincipal> {

    @Override
    public @NonNull SecurityContext createSecurityContext(WithMockPrincipal withMockPrincipal) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserPrincipal principal = UserPrincipal.create(withMockPrincipal.userId(), withMockPrincipal.isAdmin());

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        context.setAuthentication(authentication);

        return context;
    }
}
