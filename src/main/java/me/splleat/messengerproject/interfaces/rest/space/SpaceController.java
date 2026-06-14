package me.splleat.messengerproject.interfaces.rest.space;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.SpaceChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.SpaceChannelEnterUseCase;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.space.*;
import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.application.space.dto.SpaceResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.SpaceChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.space.dto.SpaceCreateRequest;
import me.splleat.messengerproject.interfaces.rest.space.dto.SpaceInviteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spaces")
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceListGetUseCase spaceListGetUseCase;
    private final SpaceCreateUseCase spaceCreateUseCase;
    private final SpaceGetUseCase spaceGetUseCase;
    private final SpaceInviteUseCase spaceInviteUseCase;
    private final SpaceChannelCreateUseCase spaceChannelCreateUseCase;
    private final SpaceLeaveUseCase spaceLeaveUseCase;
    private final SpaceChannelEnterUseCase spaceChannelEnterUseCase;

    @PostMapping
    public ResponseEntity<Void> createSpace(
            @Valid @RequestBody SpaceCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        spaceCreateUseCase.execute(request.toCommand(userPrincipal.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<SpaceListResult>> getSpaceList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long userId = userPrincipal.getUserId();

        List<SpaceListResult> response = spaceListGetUseCase.execute(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{space-id}")
    public ResponseEntity<Void> inviteSpace(
            @PathVariable("space-id") long spaceId,
            @Valid @RequestBody SpaceInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        spaceInviteUseCase.execute(request.toCommand(userId, spaceId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{space-id}")
    public ResponseEntity<SpaceResult> getSpace(
            @PathVariable("space-id") long spaceId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        SpaceResult response = spaceGetUseCase.execute(userId, spaceId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{space-id}")
    public ResponseEntity<Void> leaveSpace(
            @PathVariable("space-id") long spaceId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        spaceLeaveUseCase.execute(userId, spaceId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{space-id}/channels")
    public ResponseEntity<Void> createSpaceChannel(
            @PathVariable("space-id") long spaceId,
            @Valid @RequestBody SpaceChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        spaceChannelCreateUseCase.execute(request.toCommand(userPrincipal.getUserId(), spaceId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{space-id}/channels/{channel-id}")
    public ResponseEntity<ChannelEnterResult> enterSpaceChannel(
            @PathVariable("space-id") long spaceId,
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        ChannelEnterResult response = spaceChannelEnterUseCase.execute(userId, spaceId, channelId);

        return ResponseEntity.ok(response);
    }
}
