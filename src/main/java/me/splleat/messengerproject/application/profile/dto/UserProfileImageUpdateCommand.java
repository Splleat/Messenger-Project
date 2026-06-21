package me.splleat.messengerproject.application.profile.dto;

public record UserProfileImageUpdateCommand(
        long userId,
        String newImageKey
) {}
