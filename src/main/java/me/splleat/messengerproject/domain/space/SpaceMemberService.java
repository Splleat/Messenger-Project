package me.splleat.messengerproject.domain.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.SpaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpaceMemberService {
    private final SpaceMemberRepository spaceMemberRepository;

    @Transactional
    public SpaceMember register(SpaceMember spaceMember) {
        if (spaceMemberRepository.existsByUserIdAndSpaceId(spaceMember.getUserId(), spaceMember.getSpaceId())) {
            throw new BusinessException(ErrorCode.SPACE_MEMBER_ALREADY_EXISTS);
        }

        return spaceMemberRepository.save(spaceMember);
    }

    @Transactional
    public void registerAll(List<SpaceMember> spaceMembers) {
        if (!spaceMembers.isEmpty()) {
            spaceMemberRepository.saveAll(spaceMembers);
        }
    }

    @Transactional(readOnly = true)
    public SpaceMember getSpaceMember(long userId, long spaceId) {
        return spaceMemberRepository.findByUserIdAndSpaceId(userId, spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Long> getAllParticipantUserIds(long spaceId) {
        return spaceMemberRepository.findAllUserIdBySpaceId(spaceId);
    }

    @Transactional(readOnly = true)
    public Optional<String> getNickname(long userId, long spaceId) {
        return spaceMemberRepository.findNicknameByUserIdAndSpaceId(userId, spaceId);
    }

    @Transactional
    public void leaveSpace(long userId, long spaceId) {
        SpaceMember spaceMember = spaceMemberRepository.findByUserIdAndSpaceId(userId, spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_MEMBER_NOT_FOUND));

        if (spaceMember.isSpaceOwner()) {
            boolean hasOtherMember = spaceMemberRepository.existsBySpaceIdAndUserIdNot(spaceId, userId);

            if (hasOtherMember) {
                throw new BusinessException(ErrorCode.SPACE_OWNER_CANNOT_LEAVE);
            }
        }

        spaceMemberRepository.deleteByUserIdAndSpaceId(userId, spaceId);
    }

    @Transactional(readOnly = true)
    public void validateParticipant(long userId, long spaceId) {
        if (!spaceMemberRepository.existsByUserIdAndSpaceId(userId, spaceId)) {
            throw new BusinessException(ErrorCode.SPACE_MEMBER_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getAlreadyJoinedUserIds(long spaceId, List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        return spaceMemberRepository.findAllUserIdBySpaceIdAndUserIdIn(spaceId, userIds);
    }
}
