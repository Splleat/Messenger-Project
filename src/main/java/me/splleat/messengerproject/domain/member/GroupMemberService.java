package me.splleat.messengerproject.domain.member;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.member.exception.GroupMemberAlreadyExistsException;
import me.splleat.messengerproject.domain.member.exception.GroupMemberNotFoundException;
import me.splleat.messengerproject.domain.member.exception.GroupOwnerLeaveException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public GroupMember register(GroupMember groupMember) {
        if (groupMemberRepository.existsByUserIdAndGroupId(groupMember.getUserId(), groupMember.getGroupId())) {
            throw new GroupMemberAlreadyExistsException();
        }

        return groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void registerAll(List<GroupMember> groupMembers) {
        groupMemberRepository.saveAll(groupMembers);
    }

    @Transactional(readOnly = true)
    public GroupMember getGroupMember(long userId, long groupId) {
        return groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(GroupMemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllParticipantUserIds(long groupId) {
        return groupMemberRepository.findAllUserIdByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public Optional<String> getNickname(long userId, long groupId) {
        return groupMemberRepository.findNicknameByUserIdAndGroupId(userId, groupId);
    }

    @Transactional
    public void leaveGroup(long userId, long groupId) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(GroupMemberNotFoundException::new);

        if (groupMember.isGroupOwner()) {
            boolean hasOtherMember = groupMemberRepository.existsByGroupIdAndUserIdNot(groupId, userId);

            if (hasOtherMember) {
                throw new GroupOwnerLeaveException();
            }
        }

        groupMemberRepository.deleteByUserIdAndGroupId(userId, groupId);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllJoinedGroupIds(long userId) {
        return groupMemberRepository.findAllGroupIdByUserId(userId);
    }

    @Transactional(readOnly = true)
    public void validateParticipant(long userId, long groupId) {
        if (!groupMemberRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new GroupMemberNotFoundException();
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getAlreadyJoinedUserIds(long groupId, List<Long> userIds) {
        return groupMemberRepository.findAllUserIdByGroupIdAndUserIdIn(groupId, userIds);
    }
}
