package me.splleat.messengerproject.support.fixture;

import me.splleat.messengerproject.domain.member.GroupMember;
import me.splleat.messengerproject.domain.member.GroupRole;

public final class GroupMemberFixture {
    public static GroupMember defaultGroupMember(long userId, long groupId) {
        return GroupMember.create(userId, groupId, "member", GroupRole.MEMBER);
    }

    public static GroupMember groupOwner(long userId, long groupId) {
        return GroupMember.create(userId, groupId, "member", GroupRole.OWNER);
    }
}
