package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.dto.GatheringStatistics;
import mitl.IntoTheHeaven.application.port.in.query.dto.GatheringWithStatistics;
import mitl.IntoTheHeaven.application.port.out.GatheringMemberData;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GatheringMember;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringQueryService implements GatheringQueryUseCase {

    private final GatheringPort gatheringPort;

    @Override
    public Gathering getGatheringDetail(GatheringId gatheringId) {
        Gathering gathering = gatheringPort.findDetailById(gatheringId.getValue())
                .orElseThrow(() -> new RuntimeException("Gathering not found"));
        
        // gatheringMembers를 LEADER 먼저, 그 다음 나이 많은 순서로 정렬
        Comparator<GatheringMember> roleComparator = Comparator.comparing(gm -> gm.getGroupMember().getRole() == GroupMemberRole.LEADER ? 0 : 1);
        Comparator<GatheringMember> birthdayComparator = Comparator.comparing(gm -> gm.getGroupMember().getMember().getBirthday());
        
        List<GatheringMember> sortedGatheringMembers = gathering.getGatheringMembers().stream()
                .sorted(roleComparator.thenComparing(birthdayComparator))
                .toList();
        
        return gathering.toBuilder()
                .gatheringMembers(sortedGatheringMembers)
                .build();
    }

    @Override
    public List<GatheringWithStatistics> getGatheringsWithStatisticsByGroupId(GroupId groupId) {
        // 1. 모임 조회 및 정렬
        List<Gathering> gatherings = gatheringPort.findAllByGroupId(groupId.getValue()).stream()
                .sorted(Comparator.comparing(Gathering::getDate).reversed())
                .toList();
        
        // 2. 통계 데이터 조회
        List<UUID> gatheringIds = gatherings.stream()
                .map(gathering -> gathering.getId().getValue())
                .toList();
        List<GatheringMemberData> memberData = gatheringPort.findGatheringMemberDataByGatheringIds(gatheringIds);
        
        // 3. 통계 계산
        Map<UUID, GatheringStatistics> statisticsMap = memberData.stream()
            .collect(Collectors.groupingBy(
                GatheringMemberData::getGatheringId,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    data -> {
                        int worshipTotal = data.stream()
                            .mapToInt(d -> d.isWorshipAttendance() ? 1 : 0)
                            .sum();
                        int gatheringTotal = data.stream()
                            .mapToInt(d -> d.isGatheringAttendance() ? 1 : 0)
                            .sum();
                        int prayerTotal = data.stream()
                            .mapToInt(d -> d.getPrayerIds().size())
                            .sum();
                            
                        return GatheringStatistics.builder()
                            .gatheringId(data.get(0).getGatheringId())
                            .totalWorshipAttendanceCount(worshipTotal)
                            .totalGatheringAttendanceCount(gatheringTotal)
                            .totalPrayerRequestCount(prayerTotal)
                            .build();
                    }
                )
            ));
        
        // 4. GatheringWithStatistics 생성
        List<GatheringWithStatistics> responses = new ArrayList<>();
        for (int i = 0; i < gatherings.size(); i++) {
            Gathering gathering = gatherings.get(i);
            GatheringStatistics stats = statisticsMap.get(gathering.getId().getValue());
            
            // 비즈니스 로직: nth 계산, null 체크
            int nth = i + 1;
            int worshipCount = stats != null ? stats.getTotalWorshipAttendanceCount() : 0;
            int gatheringCount = stats != null ? stats.getTotalGatheringAttendanceCount() : 0;
            int prayerCount = stats != null ? stats.getTotalPrayerRequestCount() : 0;
            
            responses.add(GatheringWithStatistics.builder()
                    .gathering(gathering)
                    .nth(nth)
                    .totalWorshipAttendanceCount(worshipCount)
                    .totalGatheringAttendanceCount(gatheringCount)
                    .totalPrayerRequestCount(prayerCount)
                    .build());
        }
        
        return responses;
    }
} 