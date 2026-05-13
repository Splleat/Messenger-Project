package me.splleat.messengerproject.interfaces.rest.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.GroupChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.GroupChannelEnterUseCase;
import me.splleat.messengerproject.application.channel.dto.GroupChannelEnterCommand;
import me.splleat.messengerproject.application.group.*;
import me.splleat.messengerproject.application.group.dto.GroupGetCommand;
import me.splleat.messengerproject.application.group.dto.GroupLeaveCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupListResponse;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupCreateRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupInviteRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageCursorBothResponse;
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
    public ResponseEntity<List<GroupListResponse>> getGroupList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long userId = userPrincipal.getUserId();

        List<GroupListResponse> response = groupListGetUseCase.execute(userId);

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
    public ResponseEntity<GroupResponse> getGroup(
            @PathVariable("group-id") long groupId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        GroupResponse response = groupGetUseCase.execute(GroupGetCommand.of(userId, groupId));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{group-id}")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable("group-id") long groupId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        groupLeaveUseCase.execute(GroupLeaveCommand.of(userId, groupId));

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
    public ResponseEntity<MessageCursorBothResponse> enterGroupChannel(
            @PathVariable("group-id") long groupId,
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        MessageCursorBothResponse response = groupChannelEnterUseCase.execute(GroupChannelEnterCommand.of(userId, channelId, groupId));

        return ResponseEntity.ok(response);
    }
}
