package me.splleat.messengerproject.application.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.space.dto.SpaceListResult;
import me.splleat.messengerproject.common.annotation.UseCase;
import me.splleat.messengerproject.infrastructure.persistence.querydsl.SpaceQueryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SpaceListGetUseCase {
    private final SpaceQueryRepository spaceQueryRepository;

    @Transactional(readOnly = true)
    public List<SpaceListResult> execute(long userId) {
        return spaceQueryRepository.findSpaceList(userId);
    }
}
