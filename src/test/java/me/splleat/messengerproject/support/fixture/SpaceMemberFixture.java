package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.space.SpaceMember;
import me.splleat.messengerproject.domain.space.SpaceRole;

public final class SpaceMemberFixture {
    public static SpaceMember defaultSpaceMember(long userId, long spaceId) {
        return SpaceMember.create(userId, spaceId, "member", SpaceRole.MEMBER);
    }

    public static SpaceMember spaceOwner(long userId, long spaceId) {
        return SpaceMember.create(userId, spaceId, "member", SpaceRole.OWNER);
    }
}
