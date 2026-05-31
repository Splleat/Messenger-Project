package me.splleat.messengerproject.application.group;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.group.dto.GroupListResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.GroupQueryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GroupListGetUseCase {
    private final GroupQueryRepository groupQueryRepository;

    @Transactional(readOnly = true)
    public List<GroupListResult> execute(long userId) {
        return groupQueryRepository.findGroupList(userId);
    }
}
