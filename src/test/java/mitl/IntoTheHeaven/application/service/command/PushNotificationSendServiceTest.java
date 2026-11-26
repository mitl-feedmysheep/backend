package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.application.port.out.PrayerPushCandidate;
import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
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
    private PushSubscriptionTopicPort pushSubscriptionTopicPort;

    @Mock
    private WebPushPort webPushPort;

    @Mock
    private PushDeliveryLogPort pushDeliveryLogPort;

    @Mock
    private PrayerPort prayerPort;

    private PushNotificationSendService service;

    @BeforeEach
    void setUp() {
        PushDispatchService dispatchService = new PushDispatchService(webPushPort, pushSubscriptionPort, pushDeliveryLogPort);
        service = new PushNotificationSendService(pushSubscriptionPort, pushSubscriptionTopicPort, dispatchService, prayerPort, pushDeliveryLogPort);
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

    @Nested
    @DisplayName("발송 대상")
    class SendTargets {

        @Test
        @DisplayName("PRAYER 토픽 구독자는 등록된 timezone과 무관하게 발송 대상이다")
        void allSubscribersAreTargeted_regardlessOfTimezone() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            PushSubscription sub = buildSub(memberUuid, "America/Los_Angeles");

            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER)).thenReturn(List.of(memberId));
            when(pushSubscriptionPort.findByMemberIds(List.of(memberId))).thenReturn(List.of(sub));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendDailyPrayerPush();

            verify(webPushPort).send(eq(sub), any());
        }
    }

    @Nested
    @DisplayName("회원별 내용")
    class PerMemberContent {

        @Test
        @DisplayName("회원마다 자신의 기도제목으로 알림을 만들고, 기도제목이 없는 회원은 적어보기 알림을 받는다")
        void eachMemberGetsOwnContent() {
            UUID withPrayer = UUID.randomUUID();
            UUID withoutPrayer = UUID.randomUUID();
            PushSubscription subA = buildSub(withPrayer, "Asia/Seoul");
            PushSubscription subB = buildSub(withoutPrayer, "Asia/Seoul");
            UUID prayerId = UUID.randomUUID();

            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER))
                    .thenReturn(List.of(MemberId.from(withPrayer), MemberId.from(withoutPrayer)));
            when(pushSubscriptionPort.findByMemberIds(any())).thenReturn(List.of(subA, subB));
            when(prayerPort.findUnansweredForPush(any(), any())).thenReturn(List.of(
                    new PrayerPushCandidate(prayerId, withPrayer, "부모님 건강", true, null, LocalDateTime.now())));
            when(pushDeliveryLogPort.findByMemberIdsAndTypeSince(any(), eq(mitl.IntoTheHeaven.domain.enums.PushType.DAILY_PRAYER), any()))
                    .thenReturn(List.of());
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendDailyPrayerPush();

            ArgumentCaptor<PushPayload> payloadA = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(eq(subA), payloadA.capture());
            assertThat(payloadA.getValue().body()).isEqualTo("부모님 건강");
            assertThat(payloadA.getValue().url()).startsWith("/prayers?highlight=" + prayerId);

            ArgumentCaptor<PushPayload> payloadB = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(eq(subB), payloadB.capture());
            assertThat(payloadB.getValue().url()).startsWith("/prayers?write=1");
        }
    }

    @Nested
    @DisplayName("토픽 구독자 없음")
    class NoTopicSubscribers {

        @Test
        @DisplayName("PRAYER 토픽 구독자가 없으면 웹푸시를 보내지 않는다")
        void skipsWhenNoTopicSubscribers() {
            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER)).thenReturn(List.of());

            service.sendDailyPrayerPush();

            verify(webPushPort, never()).send(any(), any());
            verify(pushSubscriptionPort, never()).findByMemberIds(any());
        }
    }

    @Nested
    @DisplayName("발송 실패 처리")
    class SendResultHandling {

        @Test
        @DisplayName("GONE 응답이면 구독을 삭제한다")
        void gone_deletesSubscription() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");

            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER)).thenReturn(List.of(memberId));
            when(pushSubscriptionPort.findByMemberIds(List.of(memberId))).thenReturn(List.of(sub));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.GONE);

            service.sendDailyPrayerPush();

            verify(pushSubscriptionPort).deleteByEndpoint(sub.getEndpoint());
        }

        @Test
        @DisplayName("TRANSIENT_FAIL이면 구독을 삭제하지 않는다")
        void transientFail_keepsSubscription() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            PushSubscription sub = buildSub(memberUuid, "Asia/Seoul");

            when(pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER)).thenReturn(List.of(memberId));
            when(pushSubscriptionPort.findByMemberIds(List.of(memberId))).thenReturn(List.of(sub));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.TRANSIENT_FAIL);

            service.sendDailyPrayerPush();

            verify(pushSubscriptionPort, never()).deleteByEndpoint(any());
        }
    }
}
