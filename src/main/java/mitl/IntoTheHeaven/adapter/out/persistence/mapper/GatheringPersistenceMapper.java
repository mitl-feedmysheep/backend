package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GatheringPersistenceMapper {

    private final MemberPersistenceMapper memberPersistenceMapper;

    public GatheringPersistenceMapper(MemberPersistenceMapper memberPersistenceMapper) {
        this.memberPersistenceMapper = memberPersistenceMapper;
    }

    public Gathering toDomain(GatheringJpaEntity entity) {
        return Gathering.builder()
                .id(GatheringId.from(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .date(entity.getDate())
                .startedAt(entity.getStartedAt())
                .endedAt(entity.getEndedAt())
                .place(entity.getPlace())
                .gatheringMembers(entity.getGatheringMembers().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList()))
                .build();
    }

    private GatheringMember toDomain(GatheringMemberJpaEntity entity) {
        return GatheringMember.builder()
                .id(GatheringMemberId.from(entity.getId()))
                .gatheringId(GatheringId.from(entity.getGathering().getId())) // 단방향 참조: ID만 사용
                .groupMember(memberPersistenceMapper.toGroupMemberDomain(entity.getGroupMember()))
                .worshipAttendance(entity.isWorshipAttendance())
                .gatheringAttendance(entity.isGatheringAttendance())
                .story(entity.getStory())
                .prayers(entity.getPrayers().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList()))
                .build();
    }

    private Prayer toDomain(PrayerJpaEntity entity) {
        return Prayer.builder()
                .id(PrayerId.from(entity.getId()))
                .member(entity.getMember() != null ? memberPersistenceMapper.toDomain(entity.getMember()) : null)
                .gatheringMember(null) // 순환 참조 방지를 위해 null 설정
                .prayerRequest(entity.getPrayerRequest())
                .description(entity.getDescription())
                .isAnswered(entity.isAnswered())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public GatheringJpaEntity toEntity(Gathering domain, UUID groupId) {
        GroupJpaEntity groupEntity = GroupJpaEntity.builder().id(groupId).build();
        return toEntity(domain, groupEntity);
    }

    public GatheringJpaEntity toEntity(Gathering domain, GroupJpaEntity groupEntity) {
        // 먼저 Gathering 엔터티를 생성 (GatheringMember 없이)
        GatheringJpaEntity gatheringEntity = GatheringJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .date(domain.getDate())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .place(domain.getPlace())
                .group(groupEntity)
                .build();
        
        // GatheringMember들을 GatheringMemberJpaEntity로 변환 (Prayer 포함)
        Set<GatheringMemberJpaEntity> gatheringMemberEntities = domain.getGatheringMembers().stream()
                .map(gatheringMember -> toGatheringMemberEntityWithPrayers(gatheringMember, gatheringEntity))
                .collect(Collectors.toSet());
        
        // 완성된 엔터티를 다시 빌드하여 GatheringMember 리스트 설정
        return GatheringJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .date(domain.getDate())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .place(domain.getPlace())
                .group(groupEntity)
                .gatheringMembers(gatheringMemberEntities)
                .build();
    }
    
    private GatheringMemberJpaEntity toGatheringMemberEntity(GatheringMember domain) {
        return GatheringMemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .groupMember(GroupMemberJpaEntity.builder()
                        .id(domain.getGroupMember().getId().getValue())
                        .build())
                .worshipAttendance(domain.isWorshipAttendance())
                .gatheringAttendance(domain.isGatheringAttendance())
                .story(domain.getStory())
                .build();
    }

    private GatheringMemberJpaEntity toGatheringMemberEntityWithPrayers(GatheringMember domain, GatheringJpaEntity gatheringEntity) {
        // 먼저 GatheringMemberJpaEntity 기본 구조 생성
        GatheringMemberJpaEntity gatheringMemberEntity = GatheringMemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .groupMember(GroupMemberJpaEntity.builder()
                        .id(domain.getGroupMember().getId().getValue())
                        .build())
                .gathering(gatheringEntity)
                .worshipAttendance(domain.isWorshipAttendance())
                .gatheringAttendance(domain.isGatheringAttendance())
                .story(domain.getStory())
                .build();

        // Prayer들을 변환하여 설정
        List<PrayerJpaEntity> prayerEntities = domain.getPrayers().stream()
                .map(prayer -> PrayerJpaEntity.builder()
                        .id(prayer.getId().getValue())
                        .prayerRequest(prayer.getPrayerRequest())
                        .description(prayer.getDescription())
                        .isAnswered(prayer.isAnswered())
                        .createdAt(prayer.getCreatedAt())
                        .member(prayer.getMember() != null ? 
                                memberPersistenceMapper.toEntity(prayer.getMember()) : null)
                        .gatheringMember(gatheringMemberEntity)
                        .build())
                .collect(Collectors.toList());

        // 완성된 엔터티에 Prayer 리스트 설정
        return GatheringMemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .groupMember(GroupMemberJpaEntity.builder()
                        .id(domain.getGroupMember().getId().getValue())
                        .build())
                .gathering(gatheringEntity)
                .worshipAttendance(domain.isWorshipAttendance())
                .gatheringAttendance(domain.isGatheringAttendance())
                .story(domain.getStory())
                .prayers(prayerEntities)
                .build();
    }
} 