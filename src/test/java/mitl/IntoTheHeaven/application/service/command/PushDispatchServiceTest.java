package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.application.service.command.PushDispatchService.PushMessage;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushDispatchService")
class PushDispatchServiceTest {

    @Mock
    private WebPushPort webPushPort;

    @Mock
    private PushSubscriptionPort pushSubscriptionPort;

    @Mock
    private PushDeliveryLogPort pushDeliveryLogPort;

    private PushDispatchService service;
    private MemberId memberId;
    private PushSubscription subscription;

    @BeforeEach
    void setUp() {
        service = new PushDispatchService(webPushPort, pushSubscriptionPort, pushDeliveryLogPort);
        memberId = MemberId.from(UUID.randomUUID());
        subscription = PushSubscription.of(memberId, "https://push.example.com/ep", "p", "a", "ua", "Asia/Seoul");
    }

    private PushMessage message() {
        return PushMessage.of(PushType.DAILY_PRAYER, "제목", "본문", "/prayers")
                .withTemplate("PRAYER_A_1")
                .withEntity("PRAYER", "prayer-id");
    }

    @Nested
    @DisplayName("payload")
    class Payload {

        @Test
        @DisplayName("deliveryId와 type을 담고, URL에 pd 쿼리를 붙인다")
        void includesDeliveryIdTypeAndQuery() {
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.send(subscription, message());

            ArgumentCaptor<PushPayload> captor = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(eq(subscription), captor.capture());
            PushPayload payload = captor.getValue();
            assertThat(payload.type()).isEqualTo("DAILY_PRAYER");
            assertThat(payload.deliveryId()).isNotBlank();
            assertThat(payload.url()).isEqualTo("/prayers?pd=" + payload.deliveryId());
        }

        @Test
        @DisplayName("기존 쿼리와 fragment가 있어도 pd를 올바르게 붙인다")
        void appendsToExistingQuery() {
            UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
            assertThat(PushDispatchService.appendDeliveryParam("/prayers?highlight=x#top", id))
                    .isEqualTo("/prayers?highlight=x&pd=" + id + "#top");
            assertThat(PushDispatchService.appendDeliveryParam(null, id))
                    .isEqualTo("/?pd=" + id);
        }
    }

    @Nested
    @DisplayName("발송 로그")
    class DeliveryLog {

        @Test
        @DisplayName("payload의 deliveryId와 같은 ID로 결과를 기록한다")
        void recordsWithSameId() {
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.send(subscription, message());

            ArgumentCaptor<PushPayload> payloadCaptor = ArgumentCaptor.forClass(PushPayload.class);
            verify(webPushPort).send(any(), payloadCaptor.capture());
            ArgumentCaptor<PushDeliveryLog> logCaptor = ArgumentCaptor.forClass(PushDeliveryLog.class);
            verify(pushDeliveryLogPort).save(logCaptor.capture());

            PushDeliveryLog log = logCaptor.getValue();
            assertThat(log.getId().toString()).isEqualTo(payloadCaptor.getValue().deliveryId());
            assertThat(log.getMemberId()).isEqualTo(memberId);
            assertThat(log.getSubscriptionId()).isEqualTo(subscription.getId().getValue());
            assertThat(log.getPushType()).isEqualTo(PushType.DAILY_PRAYER);
            assertThat(log.getTemplateKey()).isEqualTo("PRAYER_A_1");
            assertThat(log.getEntityType()).isEqualTo("PRAYER");
            assertThat(log.getEntityId()).isEqualTo("prayer-id");
            assertThat(log.getSendResult()).isEqualTo("SUCCESS");
            assertThat(log.getSentAt()).isNotNull();
            assertThat(log.getClickedAt()).isNull();
        }

        @Test
        @DisplayName("로그 저장이 실패해도 발송 결과를 반환한다")
        void logFailureDoesNotBreakSend() {
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);
            doThrow(new RuntimeException("db down")).when(pushDeliveryLogPort).save(any());

            SendResult result = service.send(subscription, message());

            assertThat(result).isEqualTo(SendResult.SUCCESS);
        }
    }

    @Nested
    @DisplayName("만료 구독 정리")
    class ExpiredSubscription {

        @Test
        @DisplayName("GONE/INVALID면 구독을 삭제하고 결과도 기록한다")
        void goneOrInvalid_deletes() {
            when(webPushPort.send(any(), any())).thenReturn(SendResult.GONE, SendResult.INVALID);

            service.send(subscription, message());
            service.send(subscription, message());

            verify(pushSubscriptionPort, times(2)).deleteByEndpoint(subscription.getEndpoint());
            verify(pushDeliveryLogPort, times(2)).save(any());
        }

        @Test
        @DisplayName("TRANSIENT_FAIL이면 구독을 유지한다")
        void transientFail_keeps() {
            when(webPushPort.send(any(), any())).thenReturn(SendResult.TRANSIENT_FAIL);

            service.send(subscription, message());

            verify(pushSubscriptionPort, never()).deleteByEndpoint(any());
        }
    }

    @Nested
    @DisplayName("대상 조회")
    class Targets {

        @Test
        @DisplayName("sendToMembers는 회원들의 모든 기기로 발송한다")
        void sendToMembers_sendsToAllDevices() {
            PushSubscription second = PushSubscription.of(memberId, "https://push.example.com/ep2", "p", "a", "ua", "Asia/Seoul");
            when(pushSubscriptionPort.findByMemberIds(List.of(memberId))).thenReturn(List.of(subscription, second));
            when(webPushPort.send(any(), any())).thenReturn(SendResult.SUCCESS);

            service.sendToMembers(List.of(memberId), message());

            verify(webPushPort, times(2)).send(any(), any());
            verify(pushDeliveryLogPort, times(2)).save(any());
        }

        @Test
        @DisplayName("대상 회원이 없으면 아무것도 하지 않는다")
        void sendToMembers_empty() {
            service.sendToMembers(List.of(), message());

            verifyNoInteractions(pushSubscriptionPort, webPushPort, pushDeliveryLogPort);
        }
    }
}
