package me.splleat.messengerproject.interfaces.rest.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.splleat.messengerproject.interfaces.rest.auth.dto.LoginRequest;
import me.splleat.messengerproject.interfaces.rest.auth.dto.LoginResponse;
import me.splleat.messengerproject.interfaces.rest.auth.dto.LogoutRequest;
import me.splleat.messengerproject.interfaces.rest.auth.dto.RegisterRequest;
import me.splleat.messengerproject.interfaces.rest.auth.dto.TokenReissueRequest;
import me.splleat.messengerproject.interfaces.rest.auth.dto.TokenReissueResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@SecurityRequirements // 인증 없이 호출되는 API라 전역 bearerAuth 요구사항을 해제
public interface AuthApi {

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하고 액세스·리프레시 토큰을 발급받는다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "회원가입", description = "신규 사용자를 등록한다.")
    @ApiResponse(responseCode = "201", description = "가입 성공")
    ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 무효화한다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    ResponseEntity<Void> logout(@RequestBody LogoutRequest request);

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급한다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공")
    ResponseEntity<TokenReissueResult> reissue(@Valid @RequestBody TokenReissueRequest request);
}
