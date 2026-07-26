package me.splleat.messengerproject.interfaces.rest.channel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.splleat.messengerproject.application.channel.dto.ChannelEnterResult;
import me.splleat.messengerproject.application.channel.dto.ChannelListResult;
import me.splleat.messengerproject.application.channel.dto.ChannelMessagePageResult;
import me.splleat.messengerproject.application.channel.dto.ChannelParticipantResult;
import me.splleat.messengerproject.infrastructure.security.UserPrincipal;
import me.splleat.messengerproject.interfaces.rest.channel.dto.ChannelReadMessageUpdateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelCreateRequest;
import me.splleat.messengerproject.interfaces.rest.channel.dto.DirectChannelInviteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Channel", description = "채널(다이렉트 메시지) 관련 API")
public interface ChannelApi {

    @Operation(summary = "채널 목록 조회", description = "로그인한 사용자가 참여 중인 다이렉트 채널 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<ChannelListResult>> getDirectChannels(@AuthenticationPrincipal UserPrincipal userPrincipal);

    @Operation(summary = "다이렉트 채널 생성", description = "상대방과의 다이렉트 채널을 생성한다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    ResponseEntity<Void> createDirectChannel(
            @Valid @RequestBody DirectChannelCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "채널 나가기", description = "참여 중인 채널에서 나간다.")
    @ApiResponse(responseCode = "204", description = "나가기 성공")
    ResponseEntity<Void> leaveChannel(
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "채널 참여자 조회", description = "채널에 참여 중인 사용자 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<ChannelParticipantResult>> getChannelParticipants(
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "채널 입장", description = "채널에 입장하여 채널 정보를 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<ChannelEnterResult> channelEnter(
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "채널 메시지 커서 조회", description = "채널의 메시지를 커서 기반 페이지네이션으로 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<ChannelMessagePageResult> getChannelMessagesByCursor(
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @Parameter(description = "커서 메시지 ID") @RequestParam("cursorId") long cursorId,
            @Parameter(description = "조회 방향 (PREV/NEXT)") @RequestParam("direction") String direction,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "채널 초대", description = "채널에 사용자를 초대한다.")
    @ApiResponse(responseCode = "201", description = "초대 성공")
    ResponseEntity<Void> inviteDirectChannel(
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @Valid @RequestBody DirectChannelInviteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );

    @Operation(summary = "마지막 읽은 메시지 갱신", description = "채널에서 마지막으로 읽은 메시지를 갱신한다.")
    @ApiResponse(responseCode = "204", description = "갱신 성공")
    ResponseEntity<Void> updateLastRead(
            @Parameter(description = "채널 ID") @PathVariable("channel-id") long channelId,
            @Valid @RequestBody ChannelReadMessageUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    );
}
