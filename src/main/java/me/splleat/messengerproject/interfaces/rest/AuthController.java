package me.splleat.messengerproject.interfaces.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.auth.LoginUseCase;
import me.splleat.messengerproject.application.command.LoginCommand;
import me.splleat.messengerproject.interfaces.rest.request.LoginRequest;
import me.splleat.messengerproject.interfaces.rest.response.LoginResponse;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = LoginCommand.from(request);
        LoginResponse response = LoginResponse.from(loginUseCase.execute(command));

        return ResponseEntity.ok(response);
    }
}
