package me.splleat.messengerproject.interfaces.rest.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.MyProfileGetUseCase;
import me.splleat.messengerproject.application.profile.TargetProfileGetUseCase;
import me.splleat.messengerproject.application.profile.UserProfileImageUpdateUseCase;
import me.splleat.messengerproject.application.profile.UserProfileUpdateUseCase;
import me.splleat.messengerproject.application.profile.dto.UserProfileDetailResult;
import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.profile.dto.UserProfileImageUpdateRequest;
import me.splleat.messengerproject.interfaces.rest.profile.dto.UserProfileUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class UserProfileController {
    private final MyProfileGetUseCase myProfileGetUseCase;
    private final TargetProfileGetUseCase targetProfileGetUseCase;
    private final UserProfileUpdateUseCase userProfileUpdateUseCase;
    private final UserProfileImageUpdateUseCase userProfileImageUpdateUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDetailResult> getMyProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        UserProfileDetailResult result = myProfileGetUseCase.execute(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResult> getTargetProfile(
            @PathVariable long id
    ) {
        UserProfileResult result = targetProfileGetUseCase.execute(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        userProfileUpdateUseCase.execute(request.toCommand(userId));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateProfileImage(
            @Valid @RequestBody UserProfileImageUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        long userId = userPrincipal.getUserId();

        userProfileImageUpdateUseCase.execute(request.toCommand(userId));

        return ResponseEntity.noContent().build();
    }
}
