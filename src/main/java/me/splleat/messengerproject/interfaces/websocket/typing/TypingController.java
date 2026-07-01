package me.splleat.messengerproject.interfaces.websocket.typing;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.typing.SendTypingUseCase;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.typing.dto.TypingRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TypingController {
    private final SendTypingUseCase sendTypingUseCase;

    @MessageMapping("/channels/{channel-id}/typing")
    public void sendTypingEvent(
            @DestinationVariable("channel-id") long channelId,
            @Payload TypingRequest typingRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        sendTypingUseCase.execute(typingRequest.toCommand(userId, channelId));
    }
}
