package me.splleat.messengerproject.domain.space;

public enum SpaceRole {
    OWNER,
    ADMIN,
    MEMBER;

    public boolean hasPermission(SpaceRole required) {
        return switch (required) {
            case MEMBER -> true;
            case ADMIN -> this == OWNER || this == ADMIN;
            case OWNER -> this == OWNER;
        };
    }
}
