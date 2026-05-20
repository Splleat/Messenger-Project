package me.splleat.messengerproject.interfaces.rest.channel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.*;
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantGetCommand;
import me.splleat.messengerproject.application.channel.dto.DirectChannelEnterCommand;
import me.splleat.messengerproject.application.channel.dto.DirectChannelLeaveCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.*;
import me.splleat.messengerproject.application.channel.dto.ChannelMessageCursorCommand;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelMessagePageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final DirectChannelLeaveUseCase directChannelLeaveUseCase;
    private final ChannelListGetUseCase channelListGetUseCase;
    private final DirectChannelCreateUseCase directChannelCreateUseCase;
    private final DirectChannelInviteUseCase directChannelInviteUseCase;
    private final DirectChannelEnterUseCase directChannelEnterUseCase;
    private final ChannelParticipantGetUseCase channelParticipantGetUseCase;
    private final ChannelMessageCursorGetUseCase channelMessageCursorGetUseCase;

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

    @DeleteMapping("/{channel-id}")
    public ResponseEntity<Void> leaveChannel(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        directChannelLeaveUseCase.execute(DirectChannelLeaveCommand.of(userId, channelId));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{channel-id}/participants")
    public ResponseEntity<List<ChannelParticipantResponse>> getChannelParticipants(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        List<ChannelParticipantResponse> response = channelParticipantGetUseCase.execute(ChannelParticipantGetCommand.of(userId, channelId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{channel-id}")
    public ResponseEntity<ChannelEnterResponse> channelEnter(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        ChannelEnterResponse response = directChannelEnterUseCase.execute(DirectChannelEnterCommand.of(userId, channelId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{channel-id}/messages")
    public ResponseEntity<ChannelMessagePageResponse> getChannelMessagesByCursor(
            @PathVariable("channel-id") long channelId,
            @RequestParam("cursorId") long cursorId,
            @RequestParam("direction") String direction,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        ChannelMessagePageResponse response = channelMessageCursorGetUseCase.execute(ChannelMessageCursorCommand.of(userId, channelId, cursorId, direction));

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
