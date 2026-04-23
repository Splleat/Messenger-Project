package me.splleat.messengerproject.application.auth.dto;

public record RegisterCommand(
        String name,
        String email,
        String password
) {}
