package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeGoalResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeMyPrayerResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomePrayerResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeSummaryResponse;
import mitl.IntoTheHeaven.application.port.in.query.HomeQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.application.port.out.HomeSummaryData;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeQueryService implements HomeQueryUseCase {

    static final int HOME_MY_PRAYER_LIMIT = 3;

    private final GatheringPort gatheringPort;
    private final PrayerPort prayerPort;

    @Override
    public HomeSummaryResponse getHomeSummary(MemberId memberId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate sunday = today.getDayOfWeek() == DayOfWeek.SUNDAY
                ? today
                : today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate saturday = sunday.plusDays(6);

        List<HomeSummaryData> data = gatheringPort.findRecentGatheringMemberData(
                memberId.getValue(), sunday, saturday);

        // findAllByMemberId는 createdAt desc 정렬
        List<Prayer> activeMyPrayers = prayerPort.findAllByMemberId(memberId.getValue()).stream()
                .filter(Prayer::isPersonal)
                .filter(p -> !p.isAnswered())
                .toList();
        List<HomeMyPrayerResponse> myPrayers = activeMyPrayers.stream()
                .limit(HOME_MY_PRAYER_LIMIT)
                .map(p -> HomeMyPrayerResponse.builder()
                        .id(p.getId().getValue())
                        .prayerRequest(p.getPrayerRequest())
                        .createdAt(p.getCreatedAt())
                        .build())
                .toList();

        if (data.isEmpty()) {
            return HomeSummaryResponse.builder()
                    .goals(List.of())
                    .prayers(List.of())
                    .myPrayers(myPrayers)
                    .myPrayerCount(activeMyPrayers.size())
                    .build();
        }

        List<HomeGoalResponse> goals = data.stream()
                .filter(d -> d.getGoal() != null && !d.getGoal().isBlank())
                .map(d -> HomeGoalResponse.builder()
                        .groupName(d.getGroupName())
                        .goal(d.getGoal())
                        .build())
                .toList();

        List<UUID> gmIds = data.stream().map(HomeSummaryData::getGatheringMemberId).toList();
        Map<UUID, String> gmIdToGroupName = data.stream()
                .collect(Collectors.toMap(
                        HomeSummaryData::getGatheringMemberId,
                        HomeSummaryData::getGroupName,
                        (a, b) -> a));

        List<Prayer> prayers = prayerPort.findByGatheringMemberIds(gmIds);
        List<HomePrayerResponse> prayerResponses = prayers.stream()
                .filter(p -> p.getPrayerRequest() != null && !p.getPrayerRequest().isBlank())
                .map(p -> HomePrayerResponse.builder()
                        .groupName(p.getGatheringMemberId() != null
                                ? gmIdToGroupName.get(p.getGatheringMemberId().getValue())
                                : null)
                        .prayerRequest(p.getPrayerRequest())
                        .description(p.getDescription())
                        .isAnswered(p.isAnswered())
                        .build())
                .toList();

        return HomeSummaryResponse.builder()
                .goals(goals)
                .prayers(prayerResponses)
                .myPrayers(myPrayers)
                .myPrayerCount(activeMyPrayers.size())
                .build();
    }
}
