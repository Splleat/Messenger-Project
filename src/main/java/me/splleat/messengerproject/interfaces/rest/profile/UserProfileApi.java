package me.splleat.messengerproject.interfaces.rest.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.splleat.messengerproject.application.profile.dto.UserProfileDetailResult;
import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.profile.dto.UserProfileImageUpdateRequest;
import me.splleat.messengerproject.interfaces.rest.profile.dto.UserProfileUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "UserProfile", description = "사용자 프로필 관련 API")
public interface UserProfileApi {

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자 본인의 상세 프로필을 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<UserProfileDetailResult> getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal);

    @Operation(summary = "다른 사용자 프로필 조회", description = "지정한 사용자 ID의 프로필을 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<UserProfileResult> getTargetProfile(@Parameter(description = "사용자 ID") @PathVariable long id);

    @Operation(summary = "사용자 검색", description = "이름으로 다른 사용자 프로필을 검색한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<UserProfileResult>> searchUserProfiles(
            @Parameter(description = "검색할 이름") @RequestParam("name") String name,
            @Parameter(description = "페이지 번호") @RequestParam("page") int page,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "프로필 수정", description = "닉네임 등 프로필 정보를 수정한다.")
    @ApiResponse(responseCode = "204", description = "수정 성공")
    ResponseEntity<Void> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "프로필 이미지 수정", description = "프로필 이미지를 수정한다.")
    @ApiResponse(responseCode = "204", description = "수정 성공")
    ResponseEntity<Void> updateProfileImage(
            @Valid @RequestBody UserProfileImageUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );
}
