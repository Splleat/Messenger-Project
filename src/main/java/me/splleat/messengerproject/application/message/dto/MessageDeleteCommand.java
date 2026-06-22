package me.splleat.messengerproject.application.message.dto;

public record MessageDeleteCommand(
        long userId,
        long messageId
) {}
