package me.splleat.messengerproject.interfaces.rest.space;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.application.space.dto.SpaceResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.SpaceChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.space.dto.SpaceCreateRequest;
import me.splleat.messengerproject.interfaces.rest.space.dto.SpaceInviteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Space", description = "스페이스(그룹) 관련 API")
public interface SpaceApi {

    @Operation(summary = "스페이스 생성", description = "새로운 스페이스를 생성한다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    ResponseEntity<Void> createSpace(
            @Valid @RequestBody SpaceCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "스페이스 목록 조회", description = "로그인한 사용자가 참여 중인 스페이스 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<SpaceListResult>> getSpaceList(@AuthenticationPrincipal UserPrincipal userPrincipal);

    @Operation(summary = "스페이스 초대", description = "스페이스에 사용자를 초대한다.")
    @ApiResponse(responseCode = "201", description = "초대 성공")
    ResponseEntity<Void> inviteSpace(
            @Parameter(description = "스페이스 ID") @PathVariable("space-id") long spaceId,
            @Valid @RequestBody SpaceInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "스페이스 조회", description = "스페이스 상세 정보를 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<SpaceResult> getSpace(
            @Parameter(description = "스페이스 ID") @PathVariable("space-id") long spaceId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "스페이스 나가기", description = "참여 중인 스페이스에서 나간다.")
    @ApiResponse(responseCode = "204", description = "나가기 성공")
    ResponseEntity<Void> leaveSpace(
            @Parameter(description = "스페이스 ID") @PathVariable("space-id") long spaceId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "스페이스 채널 생성", description = "스페이스 안에 새 채널을 생성한다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    ResponseEntity<Void> createSpaceChannel(
            @Parameter(description = "스페이스 ID") @PathVariable("space-id") long spaceId,
            @Valid @RequestBody SpaceChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "스페이스 채널 입장", description = "스페이스 안의 채널에 입장하여 채널 정보를 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<ChannelEnterResult> enterSpaceChannel(
            @Parameter(description = "스페이스 ID") @PathVariable("space-id") long spaceId,
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );
}
