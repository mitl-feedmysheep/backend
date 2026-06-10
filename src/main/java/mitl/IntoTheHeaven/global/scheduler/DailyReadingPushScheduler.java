package mitl.IntoTheHeaven.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingCompletionHistoryJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingPlanDayJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "webpush.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class DailyReadingPushScheduler {

    private static final int PUSH_HOUR = 7;

    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    private final ReadingPlanDayJpaRepository readingPlanDayJpaRepository;
    private final ReadingCompletionHistoryJpaRepository readingCompletionJpaRepository;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;
    private final PushSubscriptionPort pushSubscriptionPort;
    private final WebPushPort webPushPort;

    /**
     * 매시 실행, 구독자 timezone 기준으로 현지 7시에 발송
     */
    @Scheduled(cron = "0 0 * * * *")
    public void sendDailyReadingPush() {
        LocalDate today = LocalDate.now();

        departmentReadingPlanJpaRepository.findAllActiveByDate(today).forEach(mapping -> {
            UUID planId = mapping.getReadingPlan().getId();
            UUID departmentId = mapping.getDepartment().getId();
            UUID deptPlanId = mapping.getId();

            // 오늘 분량 없으면 스킵
            var dayOpt = readingPlanDayJpaRepository.findByReadingPlanIdAndReadingDate(planId, today);
            if (dayOpt.isEmpty()) return;
            var day = dayOpt.get();

            // 오늘 이미 완독한 멤버 ID 집합
            Set<UUID> completedMemberIds = Set.copyOf(
                    readingCompletionJpaRepository.findMemberIdsByDeptPlanIdAndDate(deptPlanId, today));

            // ACTIVE 멤버 중 미완독자
            List<MemberId> targetMemberIds = departmentMemberJpaRepository
                    .findByDepartmentIdAndStatus(departmentId, DepartmentMemberStatus.ACTIVE)
                    .stream()
                    .map(dm -> dm.getMember().getId())
                    .filter(id -> !completedMemberIds.contains(id))
                    .map(MemberId::from)
                    .toList();

            if (targetMemberIds.isEmpty()) return;

            // timezone 기준 7시 필터 후 발송
            List<PushSubscription> subscriptions = pushSubscriptionPort.findByMemberIds(targetMemberIds)
                    .stream()
                    .filter(sub -> isLocalHour(sub.getTimezone(), PUSH_HOUR))
                    .collect(Collectors.toList());

            if (subscriptions.isEmpty()) return;

            PushPayload payload = new PushPayload(
                    "오늘의 리딩지저스",
                    day.getReadingRange(),
                    "/reading"
            );

            for (PushSubscription sub : subscriptions) {
                SendResult result = webPushPort.send(sub, payload);
                if (result == SendResult.GONE || result == SendResult.INVALID) {
                    log.info("Removing expired reading push subscription: {}", sub.getEndpoint());
                    pushSubscriptionPort.deleteByEndpoint(sub.getEndpoint());
                }
            }

            log.info("Reading push sent for department {}: {} subscription(s)", departmentId, subscriptions.size());
        });
    }

    private boolean isLocalHour(String timezone, int targetHour) {
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            return LocalTime.now(zoneId).getHour() == targetHour;
        } catch (Exception e) {
            return false;
        }
    }
}
