package me.splleat.messengerproject.application.message.dto;

public record MessageUpdateCommand(
        long userId,
        long messageId,
        String content
) {}
