package me.splleat.messengerproject.domain.message;

public record MessageRegistration(
        Message message,
        boolean isCreated
) {
    public static MessageRegistration of(Message message, boolean isCreated) {
        return new MessageRegistration(message, isCreated);
    }
}
