package me.splleat.messengerproject.domain.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;
import me.splleat.messengerproject.infrastructure.persistence.jpa.SpaceRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;

    @Transactional
    public Space register(Space space) {
        return spaceRepository.save(space);
    }

    @Transactional(readOnly = true)
    public List<Space> getSpaces(List<Long> spaceIds) {
        if (spaceIds.isEmpty()) {
            return Collections.emptyList();
        }

        return spaceRepository.findAllByIdIn(spaceIds);
    }

    @Cacheable(value = "space", key = "#spaceId")
    @Transactional(readOnly = true)
    public Space getSpace(long spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_NOT_FOUND));
    }
}
