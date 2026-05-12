package me.splleat.messengerproject.interfaces.rest.channel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.ChannelListGetUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelInviteUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelMessageGetUseCase;
import me.splleat.messengerproject.application.channel.dto.DirectChannelMessageGetCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelListResponse;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelInviteRequest;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelListGetUseCase channelListGetUseCase;
    private final DirectChannelCreateUseCase directChannelCreateUseCase;
    private final DirectChannelInviteUseCase directChannelInviteUseCase;
    private final DirectChannelMessageGetUseCase directChannelMessageGetUseCase;

    @GetMapping
    public ResponseEntity<List<ChannelListResponse>> getDirectChannels(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long userId = userPrincipal.getUserId();

        List<ChannelListResponse> response = channelListGetUseCase.execute(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createDirectChannel(
            @Valid @RequestBody DirectChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        directChannelCreateUseCase.execute(request.toCommand(userPrincipal.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{channel-id}/messages")
    public ResponseEntity<List<MessageResponse>> getChannelMessages(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        DirectChannelMessageGetCommand command = new DirectChannelMessageGetCommand(userPrincipal.getUserId(), channelId);

        List<MessageResponse> response = directChannelMessageGetUseCase.execute(command);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{channel-id}/members")
    public ResponseEntity<Void> inviteDirectChannel(
            @PathVariable("channel-id") long channelId,
            @Valid @RequestBody DirectChannelInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        directChannelInviteUseCase.execute(request.toCommand(userPrincipal.getUserId(), channelId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
