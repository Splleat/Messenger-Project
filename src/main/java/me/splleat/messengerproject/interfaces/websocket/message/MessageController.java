package me.splleat.messengerproject.interfaces.websocket.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.SendMessageUseCase;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCreateRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;

    @MessageMapping("/channels/{channel-id}/messages")
    @SendTo("/sub/channels/{channel-id}/messages")
    public MessageResponse sendMessage(
            @DestinationVariable("channel-id") Long channelId,
            @Payload MessageCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        long senderId = userPrincipal.getUserId();

        return sendMessageUseCase.execute(request.toCommand(senderId, channelId));
    }
}
