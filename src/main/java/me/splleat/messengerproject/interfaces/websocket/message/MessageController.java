package me.splleat.messengerproject.interfaces.websocket.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.SendMessageUseCase;
import me.splleat.messengerproject.application.message.dto.MessageResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCreateRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;

    @MessageMapping("/channels/{channel-id}/messages")
    @SendTo("/sub/channels/{channel-id}/messages")
    public MessageResponse sendMessage(
            @DestinationVariable("channel-id") Long channelId,
            @Payload MessageCreateRequest request,
            Principal principal) {
        Authentication authentication = (Authentication) principal;

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        MessageResult result = sendMessageUseCase.execute(request.toCommand(userPrincipal.getUserId(), channelId));

        return MessageResponse.from(result);
    }
}
