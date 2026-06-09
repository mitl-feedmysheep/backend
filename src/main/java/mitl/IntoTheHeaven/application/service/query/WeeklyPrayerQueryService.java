package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeeklyPrayerQueryService {

    private final GatheringPort gatheringPort;
    private final PrayerPort prayerPort;

    public List<Prayer> getWeeklyPrayers(MemberId memberId, ZoneId userZone) {
        LocalDate today = LocalDate.now(userZone);
        LocalDate sunday = today.getDayOfWeek() == DayOfWeek.SUNDAY
                ? today
                : today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate saturday = sunday.plusDays(6);

        List<HomeSummaryData> data = gatheringPort.findRecentGatheringMemberData(
                memberId.getValue(), sunday, saturday);

        if (data.isEmpty()) {
            return List.of();
        }

        List<UUID> gmIds = data.stream().map(HomeSummaryData::getGatheringMemberId).toList();
        return prayerPort.findByGatheringMemberIds(gmIds).stream()
                .filter(p -> p.getPrayerRequest() != null && !p.getPrayerRequest().isBlank())
                .filter(p -> !p.isAnswered())
                .toList();
    }
}
