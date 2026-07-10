package mitl.IntoTheHeaven.global.scheduler;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingPlanDayJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingCompletionHistoryJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingPlanDayJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.model.PushSubscriptionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DailyReadingPushScheduler")
class DailyReadingPushSchedulerTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int ALL_DAYS_MASK = 0b1111111; // 매일 읽는 플랜 (요일 무관하게 테스트 결정성 확보)

    @Mock
    private DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    @Mock
    private ReadingPlanDayJpaRepository readingPlanDayJpaRepository;
    @Mock
    private ReadingCompletionHistoryJpaRepository readingCompletionJpaRepository;
    @Mock
    private DepartmentMemberJpaRepository departmentMemberJpaRepository;
    @Mock
    private PushSubscriptionPort pushSubscriptionPort;
    @Mock
    private PushSubscriptionTopicPort pushSubscriptionTopicPort;
    @Mock
    private WebPushPort webPushPort;

    private DailyReadingPushScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new DailyReadingPushScheduler(
                departmentReadingPlanJpaRepository,
                readingPlanDayJpaRepository,
                readingCompletionJpaRepository,
                departmentMemberJpaRepository,
                pushSubscriptionPort,
                pushSubscriptionTopicPort,
                webPushPort);
    }

    @Nested
    @DisplayName("cron 표현식")
    class CronSchedule {

        private static final String CRON = "0 0 8 * * *";

        @Test
        @DisplayName("cron 표현식이 유효하다")
        void cronExpression_isValid() {
            assertThat(CronExpression.isValidExpression(CRON)).isTrue();
        }

        @Test
        @DisplayName("당일 8시가 지났으면 다음 실행은 다음날 8시이다 (당일 재발송 없음)")
        void cronExpression_doesNotRefireSameDayAfter8am() {
            CronExpression cron = CronExpression.parse(CRON);
            java.time.LocalDateTime base = java.time.LocalDateTime.of(2026, 4, 21, 8, 59, 56);
            java.time.LocalDateTime next = cron.next(base);
            assertThat(next).isEqualTo(java.time.LocalDateTime.of(2026, 4, 22, 8, 0, 0));
        }
    }

    @Nested
    @DisplayName("발송 대상")
    class SendTargets {

        @Test
        @DisplayName("READING 토픽 구독자가 없으면 웹푸시를 보내지 않는다")
        void skipsWhenNoTopicSubscribers() {
            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.READING)).thenReturn(List.of());

            scheduler.sendDailyReadingPush();

            verify(webPushPort, never()).send(any(), any());
            verifyNoInteractions(departmentReadingPlanJpaRepository);
        }

        @Test
        @DisplayName("미완독 구독자는 등록된 timezone과 무관하게 발송 대상이다")
        void sendsRegardlessOfSubscriptionTimezone() {
            LocalDate today = LocalDate.now(KST);
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);

            ReadingPlanJpaEntity readingPlan = ReadingPlanJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .title("성경 통독")
                    .readingDays(ALL_DAYS_MASK)
                    .build();

            DepartmentJpaEntity department = DepartmentJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .name("청년부")
                    .build();

            DepartmentReadingPlanJpaEntity mapping = DepartmentReadingPlanJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .department(department)
                    .readingPlan(readingPlan)
                    .startDate(today)
                    .endDate(today.plusDays(30))
                    .build();

            ReadingPlanDayJpaEntity day = ReadingPlanDayJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .readingPlan(readingPlan)
                    .dayNumber(1)
                    .readingRange("창세기 1-3장")
                    .build();

            MemberJpaEntity member = MemberJpaEntity.builder().id(memberUuid).build();
            DepartmentMemberJpaEntity departmentMember = DepartmentMemberJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .department(department)
                    .member(member)
                    .status(DepartmentMemberStatus.ACTIVE)
                    .build();

            PushSubscription sub = PushSubscription.builder()
                    .id(PushSubscriptionId.newId())
                    .memberId(memberId)
                    .endpoint("https://push.example.com/" + memberUuid)
                    .p256dh("p256dh")
                    .auth("auth")
                    .timezone("America/Los_Angeles")
                    .build();

            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.READING)).thenReturn(List.of(memberId));
            when(departmentReadingPlanJpaRepository.findAllActiveByDate(any())).thenReturn(List.of(mapping));
            when(readingPlanDayJpaRepository.findByReadingPlanIdAndDayNumber(readingPlan.getId(), 1))
                    .thenReturn(Optional.of(day));
            when(readingCompletionJpaRepository.findMemberIdsByDeptPlanIdAndDate(any(), any(), any()))
                    .thenReturn(List.of());
            when(departmentMemberJpaRepository.findByDepartmentIdAndStatus(department.getId(), DepartmentMemberStatus.ACTIVE))
                    .thenReturn(List.of(departmentMember));
            when(pushSubscriptionPort.findByMemberIds(List.of(memberId))).thenReturn(List.of(sub));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            scheduler.sendDailyReadingPush();

            verify(webPushPort).send(eq(sub), any());
        }
    }
}
