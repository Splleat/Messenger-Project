package me.splleat.messengerproject.interfaces.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.auth.LoginUseCase;
import me.splleat.messengerproject.application.auth.SignUpUseCase;
import me.splleat.messengerproject.interfaces.rest.request.LoginRequest;
import me.splleat.messengerproject.interfaces.rest.request.SignUpRequest;
import me.splleat.messengerproject.interfaces.rest.response.LoginResponse;
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
    private final SignUpUseCase signUpUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = LoginResponse.from(loginUseCase.execute(request.toCommand()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        signUpUseCase.execute(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
