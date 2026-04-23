package me.splleat.messengerproject.interfaces.rest.auth.dto;

import jakarta.validation.constraints.NotBlank;
import me.splleat.messengerproject.application.auth.dto.LoginCommand;

public record LoginRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
