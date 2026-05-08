package me.splleat.messengerproject.domain.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.persistence.jpa.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    @Transactional
    public Group register(Group group) {
        return groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<Group> getGroups(List<Long> groupIds) {
        return groupRepository.findAllByIdIn(groupIds);
    }
}
