package me.splleat.messengerproject.interfaces.rest.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.GroupCreateUseCase;
import me.splleat.messengerproject.application.group.GroupListGetUseCase;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.GroupListResponse;
import me.splleat.messengerproject.interfaces.rest.group.dto.GroupCreateRequest;
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

    @PostMapping
    public ResponseEntity<Void> createGroup(
            @Valid @RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        groupCreateUseCase.execute(request.toCommand(userPrincipal.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<GroupListResponse>> getGroupList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<GroupListResponse> response = groupListGetUseCase.execute(userPrincipal.getUserId()).stream()
                .map(GroupListResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
