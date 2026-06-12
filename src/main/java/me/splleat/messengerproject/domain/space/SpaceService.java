package me.splleat.messengerproject.domain.space;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.space.exception.SpaceNotFoundException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.SpaceRepository;
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

    @Transactional(readOnly = true)
    public Space getSpace(long spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(SpaceNotFoundException::new);
    }
}
