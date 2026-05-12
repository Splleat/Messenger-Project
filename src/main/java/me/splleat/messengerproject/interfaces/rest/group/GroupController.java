package me.splleat.messengerproject.interfaces.rest.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.channel.GroupChannelCreateUseCase;
import me.splleat.messengerproject.application.channel.GroupChannelMessageGetUseCase;
import me.splleat.messengerproject.application.channel.dto.GroupChannelMessageGetCommand;
import me.splleat.messengerproject.application.group.GroupCreateUseCase;
import me.splleat.messengerproject.application.group.GroupGetUseCase;
import me.splleat.messengerproject.application.group.GroupInviteUseCase;
import me.splleat.messengerproject.application.group.GroupListGetUseCase;
import me.splleat.messengerproject.application.group.dto.GroupGetCommand;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupListResponse;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupCreateRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupInviteRequest;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupResponse;
import me.splleat.messengerproject.interfaces.websocket.message.dto.MessageResponse;
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
    private final GroupChannelMessageGetUseCase groupChannelMessageGetUseCase;
    private final GroupChannelCreateUseCase groupChannelCreateUseCase;

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
    public ResponseEntity<List<MessageResponse>> getGroupChannelMessage(
            @PathVariable("group-id") long groupId,
            @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        List<MessageResponse> response = groupChannelMessageGetUseCase.execute(GroupChannelMessageGetCommand.of(userId, groupId, channelId));

        return ResponseEntity.ok(response);
    }
}
