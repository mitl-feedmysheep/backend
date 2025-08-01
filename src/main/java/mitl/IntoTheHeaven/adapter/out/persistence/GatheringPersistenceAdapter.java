package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.GatheringPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GatheringJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GatheringMemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.GatheringMemberData;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.domain.model.Gathering;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GatheringPersistenceAdapter implements GatheringPort {

    private final GatheringJpaRepository gatheringJpaRepository;
    private final GatheringMemberJpaRepository gatheringMemberJpaRepository;
    private final GatheringPersistenceMapper gatheringPersistenceMapper;

    @Override
    public List<Gathering> findAllByGroupId(UUID groupId) {
        return gatheringJpaRepository.findAllByGroupId(groupId).stream()
                .map(gatheringPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Gathering> findDetailById(UUID gatheringId) {
        return gatheringJpaRepository.findWithDetailsById(gatheringId)
                .map(gatheringPersistenceMapper::toDomain);
    }

    @Override
    public Gathering save(Gathering gathering, UUID groupId) {
        GatheringJpaEntity entity = gatheringPersistenceMapper.toEntity(gathering, groupId);
        GatheringJpaEntity savedEntity = gatheringJpaRepository.save(entity);
        return gatheringPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Gathering save(Gathering gathering) {
        // 기존 entity에서 group 정보를 조회
        GatheringJpaEntity existingEntity = gatheringJpaRepository.findById(gathering.getId().getValue())
                .orElseThrow(() -> new RuntimeException("Gathering not found for update"));
        
        GatheringJpaEntity entity = gatheringPersistenceMapper.toEntity(gathering, existingEntity.getGroup());
        GatheringJpaEntity savedEntity = gatheringJpaRepository.save(entity);
        return gatheringPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<GatheringMemberData> findGatheringMemberDataByGatheringIds(List<UUID> gatheringIds) {
        List<GatheringMemberJpaEntity> gatheringMembers = 
            gatheringMemberJpaRepository.findByGatheringIdIn(gatheringIds);
        
        return gatheringMembers.stream()
            .map(gm -> GatheringMemberData.builder()
                .gatheringId(gm.getGathering().getId())
                .worshipAttendance(gm.isWorshipAttendance())
                .gatheringAttendance(gm.isGatheringAttendance())
                .prayerIds(gm.getPrayers().stream()
                    .map(prayer -> prayer.getId())
                    .toList())
                .build())
            .toList();
    }
} 