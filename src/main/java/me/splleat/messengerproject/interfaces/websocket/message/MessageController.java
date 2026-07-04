package me.splleat.messengerproject.interfaces.websocket.message;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.message.MessageDeleteUseCase;
import me.splleat.messengerproject.application.message.MessageUpdateUseCase;
import me.splleat.messengerproject.application.message.SendMessageFacade;
import me.splleat.messengerproject.application.message.dto.MessageUpdateCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCreateRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageUpdateRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageFacade sendMessageFacade;
    private final MessageUpdateUseCase messageUpdateUseCase;
    private final MessageDeleteUseCase messageDeleteUseCase;

    @MessageMapping("/channels/{channel-id}/messages")
    public void sendMessage(
            @DestinationVariable("channel-id") Long channelId,
            @Payload MessageCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long senderId = userPrincipal.getUserId();

        sendMessageFacade.execute(request.toCommand(senderId, channelId));
    }

    @MessageMapping("/channels/{channel-id}/messages/{message-id}/update")
    public void updateMessage(
            @DestinationVariable("channel-id") long channelId,
            @DestinationVariable("message-id") long messageId,
            @Payload MessageUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long senderId = userPrincipal.getUserId();

        messageUpdateUseCase.execute(request.toCommand(senderId, messageId));
    }

    @MessageMapping("/channels/{channel-id}/messages/{message-id}/delete")
    public void deleteMessage(
            @DestinationVariable("channel-id") long channelId,
            @DestinationVariable("message-id") long messageId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long senderId = userPrincipal.getUserId();

        messageDeleteUseCase.execute(senderId, messageId);
    }

}
