package me.splleat.messengerproject.interfaces.rest.attachment.dto;

public record PresignResponse(
        String uploadUrl,
        String objectKey
) {}
