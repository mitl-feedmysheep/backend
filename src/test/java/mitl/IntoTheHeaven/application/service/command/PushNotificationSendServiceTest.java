package mitl.IntoTheHeaven.application.service.command;

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

    private PushNotificationSendService service;

    @BeforeEach
    void setUp() {
        service = new PushNotificationSendService(pushSubscriptionPort, pushSubscriptionTopicPort, webPushPort);
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
