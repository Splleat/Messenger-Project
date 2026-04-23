package me.splleat.messengerproject.interfaces.websocket.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.SendMessageUseCase;
import me.splleat.messengerproject.application.message.dto.SendMessageResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.message.dto.SendMessageRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.SendMessageResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;

    @MessageMapping("/channel/{channelId}")
    @SendTo("/channel/{channelId}")
    public SendMessageResponse sendMessage(
            @DestinationVariable Long channelId,
            SendMessageRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        SendMessageResult result = sendMessageUseCase.execute(request.toCommand(userPrincipal.getUserId(), channelId));

        return SendMessageResponse.from(result);
    }
}
