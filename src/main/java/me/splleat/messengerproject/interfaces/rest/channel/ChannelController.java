package me.splleat.messengerproject.interfaces.rest.channel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.ChannelListGetUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.DirectChannelInviteUseCase;
import me.splleat.messengerproject.application.channel.GroupChannelCreateUseCase;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelListResponse;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelInviteRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupChannelCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelListGetUseCase channelListGetUseCase;
    private final GroupChannelCreateUseCase groupChannelCreateUseCase;
    private final DirectChannelCreateUseCase directChannelCreateUseCase;
    private final DirectChannelInviteUseCase directChannelInviteUseCase;

    @PostMapping("/groups/{group-id}/channels")
    public ResponseEntity<Void> createGroupChannel(
            @PathVariable("group-id") long groupId,
            @Valid @RequestBody GroupChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        groupChannelCreateUseCase.execute(request.toCommand(userPrincipal.getUserId(), groupId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelListResponse>> getChannels(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ChannelListResponse> response = channelListGetUseCase.execute(userPrincipal.getUserId()).stream()
                .map(ChannelListResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/channels")
    public ResponseEntity<Void> createDirectChannel(
            @Valid @RequestBody DirectChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        directChannelCreateUseCase.execute(request.toCommand(userPrincipal.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/channels/{channel-id}/members")
    public ResponseEntity<Void> inviteDirectChannel(
            @PathVariable("channel-id") long channelId,
            @Valid @RequestBody DirectChannelInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        directChannelInviteUseCase.execute(request.toCommand(userPrincipal.getUserId(), channelId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
