package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Component;

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
                .member(memberPersistenceMapper.toDomain(entity.getGroupMember().getMember()))
                .name(entity.getGroupMember().getMember().getName())
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
                .prayerRequest(entity.getPrayerRequest())
                .description(entity.getDescription())
                .isAnswered(entity.isAnswered())
                .build();
    }
} 