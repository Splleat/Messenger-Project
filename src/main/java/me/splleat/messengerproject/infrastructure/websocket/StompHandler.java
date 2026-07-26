package me.splleat.messengerproject.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.domain.channel.ChannelUserSettingService;
import me.splleat.messengerproject.infrastructure.security.JwtValidator;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private static final Pattern CHANNEL_DESTINATION = Pattern.compile("^/sub/channels/(\\d+)/.+$");
    private static final Pattern USER_DESTINATION = Pattern.compile("^/sub/users/(\\d+)/.+$");

    private final JwtValidator jwtValidator;
    private final ChannelUserSettingService channelUserSettingService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            accessor = StompHeaderAccessor.wrap(message);
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }

            String token = authorizationHeader.substring(7);
            Authentication authentication = jwtValidator.validateAndGetAuthentication(token);

            accessor.setUser(authentication);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            validateSubscription(accessor);
        }

        return message;
    }

    private void validateSubscription(StompHeaderAccessor accessor) {
        long userId = extractUserId(accessor);
        String destination = accessor.getDestination();

        if (destination == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Matcher channelMatcher = CHANNEL_DESTINATION.matcher(destination);
        if (channelMatcher.matches()) {
            long channelId = Long.parseLong(channelMatcher.group(1));
            channelUserSettingService.validateParticipant(userId, channelId);
            return;
        }

        Matcher userMatcher = USER_DESTINATION.matcher(destination);
        if (userMatcher.matches()) {
            long targetUserId = Long.parseLong(userMatcher.group(1));
            if (targetUserId != userId) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
            return;
        }

        throw new BusinessException(ErrorCode.FORBIDDEN);
    }

    private long extractUserId(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof Authentication authentication)
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return principal.getUserId();
    }
}
