package me.splleat.messengerproject.domain.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    @Transactional
    public Group register(Group group) {
        return groupRepository.save(group);
    }

    public Group getReference(long groupId) {
        return groupRepository.getReferenceById(groupId);
    }
}
