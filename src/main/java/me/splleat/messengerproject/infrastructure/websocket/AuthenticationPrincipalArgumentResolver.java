package me.splleat.messengerproject.infrastructure.websocket;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        Authentication authentication = (Authentication) message.getHeaders().get("simpUser");

        if (authentication == null) {
            return null;
        }

        return authentication.getPrincipal();
    }
}
