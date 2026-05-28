package me.splleat.messengerproject.interfaces.rest.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.auth.LoginUseCase;
import me.splleat.messengerproject.application.auth.LogoutUseCase;
import me.splleat.messengerproject.application.auth.RegisterUseCase;
import me.splleat.messengerproject.application.auth.TokenReissueUseCase;
import me.splleat.messengerproject.interfaces.rest.auth.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutUseCase logoutUseCase;
    private final TokenReissueUseCase tokenReissueUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request.toCommand());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.execute(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        logoutUseCase.execute(request.toCommand());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenReissueResult> reissue(@RequestBody TokenReissueRequest request) {
        TokenReissueResult response = tokenReissueUseCase.execute(request.toCommand());

        return ResponseEntity.ok(response);
    }
}
