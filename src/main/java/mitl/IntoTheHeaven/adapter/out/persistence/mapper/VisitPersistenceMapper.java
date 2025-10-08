package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitMemberJpaEntity;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class VisitPersistenceMapper {

    private final MemberPersistenceMapper memberPersistenceMapper;

    public VisitPersistenceMapper(MemberPersistenceMapper memberPersistenceMapper) {
        this.memberPersistenceMapper = memberPersistenceMapper;
    }

    /**
     * Convert VisitJpaEntity to Visit domain model
     */
    public Visit toDomain(VisitJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Visit.builder()
                .id(VisitId.from(entity.getId()))
                .churchId(ChurchId.from(entity.getChurch().getId()))
                .pastorMemberId(ChurchMemberId.from(entity.getPastor().getId()))
                .date(entity.getDate())
                .startedAt(entity.getStartedAt())
                .endedAt(entity.getEndedAt())
                .place(entity.getPlace())
                .expense(entity.getExpense())
                .notes(entity.getNotes())
                .visitMembers(entity.getVisitMembers().stream()
                        .map(this::toVisitMemberDomain)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /**
     * Convert VisitMemberJpaEntity to VisitMember domain model
     */
    private VisitMember toVisitMemberDomain(VisitMemberJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return VisitMember.builder()
                .id(VisitMemberId.from(entity.getId()))
                .visitId(VisitId.from(entity.getVisit().getId()))
                .churchMemberId(ChurchMemberId.from(entity.getChurchMember().getId()))
                .churchMember(toChurchMemberWithMember(entity.getChurchMember()))
                .story(entity.getStory())
                .prayers(entity.getPrayers().stream()
                        .map(this::toPrayerDomain)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /**
     * Convert ChurchMemberJpaEntity to ChurchMember with Member info
     */
    private ChurchMember toChurchMemberWithMember(ChurchMemberJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ChurchMember.builder()
                .id(ChurchMemberId.from(entity.getId()))
                .churchId(ChurchId.from(entity.getChurch().getId()))
                .memberId(MemberId.from(entity.getMember().getId()))
                .member(memberPersistenceMapper.toDomain(entity.getMember()))
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /**
     * Convert PrayerJpaEntity to Prayer domain model
     */
    private Prayer toPrayerDomain(PrayerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Prayer.builder()
                .id(PrayerId.from(entity.getId()))
                .member(entity.getMember() != null 
                        ? memberPersistenceMapper.toDomain(entity.getMember()) 
                        : null)
                .visitMember(null) // Prevent circular reference
                .prayerRequest(entity.getPrayerRequest())
                .description(entity.getDescription())
                .isAnswered(entity.isAnswered())
                .createdAt(entity.getCreatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /**
     * Convert Visit domain model to VisitJpaEntity
     */
    public VisitJpaEntity toEntity(Visit domain, UUID churchId) {
        if (domain == null) {
            return null;
        }
        ChurchJpaEntity churchEntity = ChurchJpaEntity.builder()
                .id(churchId)
                .build();
        
        ChurchMemberJpaEntity pastorEntity = ChurchMemberJpaEntity.builder()
                .id(domain.getPastorMemberId().getValue())
                .build();
        
        VisitJpaEntity visitEntity = VisitJpaEntity.builder()
                .id(domain.getId().getValue())
                .church(churchEntity)
                .pastor(pastorEntity)
                .date(domain.getDate())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .place(domain.getPlace())
                .expense(domain.getExpense())
                .notes(domain.getNotes())
                .build();

        // Convert and add visit members
        List<VisitMemberJpaEntity> visitMemberEntities = domain.getVisitMembers().stream()
                .map(vm -> toVisitMemberEntity(vm, visitEntity))
                .collect(Collectors.toList());
        visitEntity.getVisitMembers().addAll(visitMemberEntities);

        return visitEntity;
    }

    /**
     * Convert VisitMember domain model to VisitMemberJpaEntity
     */
    private VisitMemberJpaEntity toVisitMemberEntity(VisitMember domain, VisitJpaEntity visitEntity) {
        if (domain == null) {
            return null;
        }
        ChurchMemberJpaEntity churchMemberEntity = ChurchMemberJpaEntity.builder()
                .id(domain.getChurchMemberId().getValue())
                .build();

        VisitMemberJpaEntity visitMemberEntity = VisitMemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .visit(visitEntity)
                .churchMember(churchMemberEntity)
                .story(domain.getStory())
                .build();

        // Convert and add prayers
        List<PrayerJpaEntity> prayerEntities = domain.getPrayers().stream()
                .map(prayer -> toPrayerEntity(prayer, visitMemberEntity))
                .collect(Collectors.toList());
        visitMemberEntity.getPrayers().addAll(prayerEntities);

        return visitMemberEntity;
    }

    /**
     * Convert Prayer domain model to PrayerJpaEntity
     */
    private PrayerJpaEntity toPrayerEntity(Prayer domain, VisitMemberJpaEntity visitMemberEntity) {
        if (domain == null) {
            return null;
        }
        return PrayerJpaEntity.builder()
                .id(domain.getId().getValue())
                .visitMember(visitMemberEntity)
                .prayerRequest(domain.getPrayerRequest())
                .description(domain.getDescription())
                .isAnswered(domain.isAnswered())
                .build();
    }
}

