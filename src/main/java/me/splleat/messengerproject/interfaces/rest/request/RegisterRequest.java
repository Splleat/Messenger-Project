package me.splleat.messengerproject.interfaces.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.splleat.messengerproject.application.command.RegisterCommand;

public record RegisterRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해야 합니다.")
        @Pattern(regexp = "^[a-zA-Z가-힣\\s]*$", message = "이름은 한글 또는 영문만 가능합니다.")
        String name,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 72, message = "비밀번호는 8자 이상 72자 이하로 입력해야 합니다.")
        String password
) {
    public RegisterCommand toCommand() {
        return new RegisterCommand(name, email, password);
    }
}