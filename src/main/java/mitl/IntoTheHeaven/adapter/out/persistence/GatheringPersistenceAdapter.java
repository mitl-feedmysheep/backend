package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.GatheringPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GatheringJpaRepository;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GroupId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GatheringPersistenceAdapter implements GatheringPort {

    private final GatheringJpaRepository gatheringJpaRepository;
    private final GatheringPersistenceMapper gatheringPersistenceMapper;

    @Override
    public List<Gathering> findAllByGroupId(GroupId groupId) {
        return gatheringJpaRepository.findAllByGroupId(groupId.getValue()).stream()
                .map(gatheringPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
} 