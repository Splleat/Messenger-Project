package me.splleat.messengerproject.interfaces.websocket.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.SendMessageUseCase;
import me.splleat.messengerproject.application.message.dto.MessageResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCreateRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;

    @MessageMapping("/channel/{channel-id}")
    @SendTo("/sub/channel/{channel-id}")
    public MessageResponse sendMessage(
            @DestinationVariable("channel-id") Long channelId,
            MessageCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MessageResult result = sendMessageUseCase.execute(request.toCommand(userPrincipal.getUserId(), channelId));

        return MessageResponse.from(result);
    }
}
