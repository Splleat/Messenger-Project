package me.splleat.messengerproject.application.profile.dto;

public record UserProfileUpdateCommand(
        long userId,
        String name,
        String statusMessage
) {}
