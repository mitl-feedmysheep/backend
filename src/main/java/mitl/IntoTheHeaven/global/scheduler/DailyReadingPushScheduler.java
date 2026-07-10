package mitl.IntoTheHeaven.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingCompletionHistoryJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingPlanDayJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.application.service.query.ReadingPlanQueryService;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    private final ReadingPlanDayJpaRepository readingPlanDayJpaRepository;
    private final ReadingCompletionHistoryJpaRepository readingCompletionJpaRepository;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;
    private final PushSubscriptionPort pushSubscriptionPort;
    private final PushSubscriptionTopicPort pushSubscriptionTopicPort;
    private final WebPushPort webPushPort;

    /**
     * 매일 오전 8시(KST), READING 토픽 구독자에게 발송
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void sendDailyReadingPush() {
        LocalDate today = LocalDate.now(KST);

        Set<UUID> readingTopicMemberIds = pushSubscriptionTopicPort
                .findMemberIdsByTopic(PushTopic.READING)
                .stream()
                .map(MemberId::getValue)
                .collect(Collectors.toSet());

        if (readingTopicMemberIds.isEmpty()) return;

        departmentReadingPlanJpaRepository.findAllActiveByDate(today).forEach(mapping -> {
            UUID planId = mapping.getReadingPlan().getId();
            String planTitle = mapping.getReadingPlan().getTitle();
            UUID departmentId = mapping.getDepartment().getId();
            UUID deptPlanId = mapping.getId();

            int readingDays = mapping.getReadingPlan().getReadingDays();
            int dayNumber = ReadingPlanQueryService.computeDayNumber(mapping.getStartDate(), today, readingDays);
            if (dayNumber == 0) return;
            var dayOpt = readingPlanDayJpaRepository.findByReadingPlanIdAndDayNumber(planId, dayNumber);
            if (dayOpt.isEmpty()) return;
            var day = dayOpt.get();

            Set<UUID> completedMemberIds = Set.copyOf(
                    readingCompletionJpaRepository.findMemberIdsByDeptPlanIdAndDate(
                            deptPlanId, today.atStartOfDay(), today.plusDays(1).atStartOfDay()));

            // READING 토픽 구독자 중 이 부서 ACTIVE 멤버 & 미완독자
            List<MemberId> targetMemberIds = departmentMemberJpaRepository
                    .findByDepartmentIdAndStatus(departmentId, DepartmentMemberStatus.ACTIVE)
                    .stream()
                    .map(dm -> dm.getMember().getId())
                    .filter(id -> readingTopicMemberIds.contains(id))
                    .filter(id -> !completedMemberIds.contains(id))
                    .map(MemberId::from)
                    .toList();

            if (targetMemberIds.isEmpty()) return;

            List<PushSubscription> subscriptions = pushSubscriptionPort.findByMemberIds(targetMemberIds);

            if (subscriptions.isEmpty()) return;

            PushPayload payload = new PushPayload(
                    "오늘의 말씀, 같이 읽어요 📖",
                    planTitle + " · " + day.getReadingRange(),
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
}
