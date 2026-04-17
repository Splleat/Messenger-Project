package me.splleat.messengerproject.application.command;

public record RegisterCommand(
        String name,
        String email,
        String password
) {}
