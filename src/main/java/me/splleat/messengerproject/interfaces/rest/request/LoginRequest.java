package me.splleat.messengerproject.interfaces.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Length(min = 8, max = 72)
        String password
) {}
