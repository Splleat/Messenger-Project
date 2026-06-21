package me.splleat.messengerproject.interfaces.rest.attachment.dto;

public record PresignRequest(
        String fileName,
        String contentType,
        long size
) {}