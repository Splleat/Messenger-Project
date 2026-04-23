package me.splleat.messengerproject.application.auth.dto;

public record LoginCommand(
        String email,
        String password
) {}
