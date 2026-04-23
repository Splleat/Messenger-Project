package me.splleat.messengerproject.application.auth.command;

public record LoginCommand(
        String email,
        String password
) {}
