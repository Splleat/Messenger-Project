package me.splleat.messengerproject.application.auth.command;

public record RegisterCommand(
        String name,
        String email,
        String password
) {}
