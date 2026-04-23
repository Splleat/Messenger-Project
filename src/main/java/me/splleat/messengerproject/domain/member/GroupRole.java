package me.splleat.messengerproject.domain.member;

public enum GroupRole {
    OWNER,
    ADMIN,
    MEMBER;

    public boolean hasPermission(GroupRole required) {
        return switch (required) {
            case MEMBER -> true;
            case ADMIN -> this == OWNER || this == ADMIN;
            case OWNER -> this == OWNER;
        };
    }
}
