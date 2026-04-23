package me.splleat.messengerproject.domain.member;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.member.exception.GroupMemberAlreadyExistsException;
import me.splleat.messengerproject.domain.member.exception.GroupMemberNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;

    public GroupMember register(GroupMember groupMember) {
        if (groupMemberRepository.existsByUserIdAndGroupId(groupMember.getUserId(), groupMember.getGroupId())) {
            throw new GroupMemberAlreadyExistsException();
        }

        return groupMemberRepository.save(groupMember);
    }

    public GroupMember getGroupMember(long userId, long groupId) {
        return groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(GroupMemberNotFoundException::new);
    }

    public List<Long> getAllGroupMemberUserId(long groupId) {
        return groupMemberRepository.findAllUserIdByGroupId(groupId);
    }
}
