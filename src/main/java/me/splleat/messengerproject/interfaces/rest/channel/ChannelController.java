package me.splleat.messengerproject.interfaces.rest.channel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.*;
import me.splleat.messengerproject.application.channel.dto.*;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {
    private final ChannelListGetUseCase channelListGetUseCase;
    private final ChannelMessageCursorGetUseCase channelMessageCursorGetUseCase;
    private final ChannelParticipantGetUseCase channelParticipantGetUseCase;
    private final ChannelReadMessageUpdateUseCase channelReadMessageUpdateUseCase;
    private final DirectChannelEnterUseCase directChannelEnterUseCase;
    private final DirectChannelCreateUseCase directChannelCreateUseCase;
    private final DirectChannelInviteUseCase directChannelInviteUseCase;
    private final DirectChannelLeaveUseCase directChannelLeaveUseCase;

    @Override
    @GetMapping
    public ResponseEntity<List<ChannelListResult>> getDirectChannels(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long userId = userPrincipal.getUserId();

        List<ChannelListResult> response = channelListGetUseCase.execute(userId);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> createDirectChannel(
            @Valid @RequestBody DirectChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        directChannelCreateUseCase.execute(request.toCommand(userPrincipal.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @DeleteMapping("/{channel-id}")
    public ResponseEntity<Void> leaveChannel(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        directChannelLeaveUseCase.execute(userId, channelId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{channel-id}/participants")
    public ResponseEntity<List<ChannelParticipantResult>> getChannelParticipants(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        List<ChannelParticipantResult> response = channelParticipantGetUseCase.execute(userId, channelId);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{channel-id}")
    public ResponseEntity<ChannelEnterResult> channelEnter(
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        ChannelEnterResult response = directChannelEnterUseCase.execute(userId, channelId);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{channel-id}/messages")
    public ResponseEntity<ChannelMessagePageResult> getChannelMessagesByCursor(
            @PathVariable("channel-id") long channelId,
            @RequestParam("cursorId") long cursorId,
            @RequestParam("direction") String direction,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        ChannelMessagePageResult response = channelMessageCursorGetUseCase.execute(ChannelMessageCursorCommand.of(userId, channelId, cursorId, direction));

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{channel-id}/members")
    public ResponseEntity<Void> inviteDirectChannel(
            @PathVariable("channel-id") long channelId,
            @Valid @RequestBody DirectChannelInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        directChannelInviteUseCase.execute(request.toCommand(userPrincipal.getUserId(), channelId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PatchMapping("/{channel-id}/read")
    public ResponseEntity<Void> updateLastRead(
            @PathVariable("channel-id") long channelId,
            @Valid @RequestBody ChannelReadMessageUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        channelReadMessageUpdateUseCase.execute(userPrincipal.getUserId(), channelId, request.lastReadMessageId());

        return ResponseEntity.noContent().build();
    }
}
