package me.splleat.messengerproject.interfaces.rest.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.GroupChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.GroupChannelEnterUseCase;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.group.*;
import me.splleat.messengerproject.application.group.dto.GroupListResult;
import me.splleat.messengerproject.application.group.dto.GroupResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupCreateRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupInviteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupListGetUseCase groupListGetUseCase;
    private final GroupCreateUseCase groupCreateUseCase;
    private final GroupGetUseCase groupGetUseCase;
    private final GroupInviteUseCase groupInviteUseCase;
    private final GroupChannelCreateUseCase groupChannelCreateUseCase;
    private final GroupLeaveUseCase groupLeaveUseCase;
    private final GroupChannelEnterUseCase groupChannelEnterUseCase;

    @PostMapping
    public ResponseEntity<Void> createGroup(
            @Valid @RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        groupCreateUseCase.execute(request.toCommand(userPrincipal.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<GroupListResult>> getGroupList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long userId = userPrincipal.getUserId();

        List<GroupListResult> response = groupListGetUseCase.execute(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{group-id}")
    public ResponseEntity<Void> inviteGroup(
            @PathVariable("group-id") long groupId,
            @Valid @RequestBody GroupInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        groupInviteUseCase.execute(request.toCommand(userId, groupId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{group-id}")
    public ResponseEntity<GroupResult> getGroup(
            @PathVariable("group-id") long groupId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        GroupResult response = groupGetUseCase.execute(userId, groupId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{group-id}")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable("group-id") long groupId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        groupLeaveUseCase.execute(userId, groupId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{group-id}/channels")
    public ResponseEntity<Void> createGroupChannel(
            @PathVariable("group-id") long groupId,
            @Valid @RequestBody GroupChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        groupChannelCreateUseCase.execute(request.toCommand(userPrincipal.getUserId(), groupId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{group-id}/channels/{channel-id}")
    public ResponseEntity<ChannelEnterResult> enterGroupChannel(
            @PathVariable("group-id") long groupId,
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        ChannelEnterResult response = groupChannelEnterUseCase.execute(userId, groupId, channelId);

        return ResponseEntity.ok(response);
    }
}
