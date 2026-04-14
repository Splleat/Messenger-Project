package me.splleat.messengerproject.application.command;

public record SignUpCommand(
        String name,
        String email,
        String password
) {}
