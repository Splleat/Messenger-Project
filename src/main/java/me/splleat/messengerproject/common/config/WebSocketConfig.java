package me.splleat.messengerproject.common.config;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.websocket.AuthenticationPrincipalArgumentResolver;
import me.splleat.messengerproject.infrastructure.websocket.StompHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JsonMapper jsonMapper;
    private final StompHandler stompHandler;
    private final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOrigins(allowedOrigins.stream().map(String::trim).toArray(String[]::new))
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authenticationPrincipalArgumentResolver);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);

        messageConverters.add(converter);

        return false;
    }
}


