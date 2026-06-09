package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.application.service.query.WeeklyPrayerQueryService;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.model.PushSubscriptionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushNotificationSendService")
class PushNotificationSendServiceTest {

    @Mock
    private PushSubscriptionPort pushSubscriptionPort;

    @Mock
    private WebPushPort webPushPort;

    @Mock
    private WeeklyPrayerQueryService weeklyPrayerQueryService;

    // 2026-04-21T00:00:00Z → Asia/Seoul은 09:00, America/Los_Angeles는 17:00(전날)
    private static final Instant FIXED_INSTANT = Instant.parse("2026-04-21T00:00:00Z");
    private Clock fixedClock;
    private PushNotificationSendService service;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
        service = new PushNotificationSendService(pushSubscriptionPort, webPushPort, weeklyPrayerQueryService, fixedClock);
    }

    private PushSubscription buildSub(UUID memberUuid, String timezone) {
        return PushSubscription.builder()
                .id(PushSubscriptionId.newId())
                .memberId(MemberId.from(memberUuid))
                .endpoint("https://push.example.com/" + memberUuid)
                .p256dh("p256dh")
                .auth("auth")
                .timezone(timezone)
                .build();
    }

    private Prayer buildPrayer(String request) {
        Prayer prayer = mock(Prayer.class);
        when(prayer.getPrayerRequest()).thenReturn(request);
        return prayer;
    }

    @Nested
    @DisplayName("타임존 필터")
    class TimezoneFilter {

        @Test
        @DisplayName("Asia/Seoul(UTC+9)은 UTC 00:00에 09:00이므로 발송 대상이다")
        void seoulAt9am_isIncluded() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");
            Prayer prayer = buildPrayer("기도제목");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of(prayer));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendDailyPrayerPush();

            verify(webPushPort).send(eq(sub), any());
        }

        @Test
        @DisplayName("America/Los_Angeles(UTC-7)은 UTC 00:00에 17:00이므로 발송 대상이 아니다")
        void losAngelesAt17_isExcluded() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "America/Los_Angeles");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));

            service.sendDailyPrayerPush();

            verify(webPushPort, never()).send(any(), any());
        }
    }

    @Nested
    @DisplayName("빈 기도제목 처리")
    class EmptyPrayers {

        @Test
        @DisplayName("기도제목이 없으면 웹푸시를 보내지 않는다")
        void skipsWhenNoPrayers() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of());

            service.sendDailyPrayerPush();

            verify(webPushPort, never()).send(any(), any());
        }
    }

    @Nested
    @DisplayName("알림 본문 생성")
    class PayloadBuilding {

        @Test
        @DisplayName("기도제목이 1개면 '외 N개' 없이 preview만 표시한다")
        void singlePrayer_noSuffix() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");
            Prayer prayer = buildPrayer("가족 건강을 위해 기도합니다");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of(prayer));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendDailyPrayerPush();

            ArgumentCaptor<PushPayload> captor = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(eq(sub), captor.capture());

            String body = captor.getValue().body();
            assertThat(body).doesNotContain("외");
            assertThat(body).contains("\"");
        }

        @Test
        @DisplayName("기도제목이 여러 개면 '외 N개'를 붙인다")
        void multiplePrayers_addsSuffix() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");
            Prayer p1 = buildPrayer("가족 건강을 위해");
            // p2, p3는 개수 계산에만 사용 (getPrayerRequest 불필요)
            Prayer p2 = mock(Prayer.class);
            Prayer p3 = mock(Prayer.class);

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of(p1, p2, p3));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendDailyPrayerPush();

            ArgumentCaptor<PushPayload> captor = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(eq(sub), captor.capture());

            String body = captor.getValue().body();
            assertThat(body).contains("외 2개");
        }

        @Test
        @DisplayName("기도제목이 18자를 초과하면 잘라서 '...'을 붙인다")
        void longPrayerRequest_truncated() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");
            Prayer prayer = buildPrayer("이것은 매우 긴 기도제목으로 18자를 초과합니다");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of(prayer));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendDailyPrayerPush();

            ArgumentCaptor<PushPayload> captor = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(eq(sub), captor.capture());

            assertThat(captor.getValue().body()).contains("...");
        }
    }

    @Nested
    @DisplayName("발송 실패 처리")
    class SendResultHandling {

        @Test
        @DisplayName("GONE 응답이면 구독을 삭제한다")
        void gone_deletesSubscription() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");
            Prayer prayer = buildPrayer("기도제목");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of(prayer));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.GONE);

            service.sendDailyPrayerPush();

            verify(pushSubscriptionPort).deleteByEndpoint(sub.getEndpoint());
        }

        @Test
        @DisplayName("TRANSIENT_FAIL이면 구독을 삭제하지 않는다")
        void transientFail_keepsSubscription() {
            UUID memberUuid = UUID.randomUUID();
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");
            Prayer prayer = buildPrayer("기도제목");

            when(pushSubscriptionPort.findAll()).thenReturn(List.of(sub));
            when(weeklyPrayerQueryService.getWeeklyPrayers(any(), any(ZoneId.class)))
                    .thenReturn(List.of(prayer));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.TRANSIENT_FAIL);

            service.sendDailyPrayerPush();

            verify(pushSubscriptionPort, never()).deleteByEndpoint(any());
        }
    }
}
