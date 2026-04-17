package me.splleat.messengerproject.infrastructure.security.dto;

public record TokenResult(
        String jti,
        String token,
        long expirationMillis
) {}
