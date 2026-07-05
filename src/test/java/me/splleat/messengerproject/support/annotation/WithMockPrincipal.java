package me.splleat.messengerproject.support.annotation;

import me.splleat.messengerproject.support.MockUserSecurityFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = MockUserSecurityFactory.class)
public @interface WithMockPrincipal {
    long userId() default 1L;
    boolean isAdmin() default false;
}
