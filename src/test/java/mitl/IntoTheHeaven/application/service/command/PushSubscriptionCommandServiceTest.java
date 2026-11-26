package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SubscribePushCommand;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.PushTopicHistoryPort;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.enums.PushTopicAction;
import mitl.IntoTheHeaven.domain.enums.PushTopicChangeSource;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.model.PushTopicHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushSubscriptionCommandService")
class PushSubscriptionCommandServiceTest {

    @Mock
    private PushSubscriptionPort pushSubscriptionPort;

    @Mock
    private PushSubscriptionTopicPort pushSubscriptionTopicPort;

    @Mock
    private PushTopicHistoryPort pushTopicHistoryPort;

    @InjectMocks
    private PushSubscriptionCommandService service;

    private MemberId memberId;
    private SubscribePushCommand command;

    @BeforeEach
    void setUp() {
        memberId = MemberId.from(UUID.randomUUID());
        command = SubscribePushCommand.builder()
                .memberId(memberId)
                .endpoint("https://push.example.com/endpoint")
                .p256dh("p256dhKey")
                .auth("authKey")
                .userAgent("Mozilla/5.0")
                .timezone("Asia/Seoul")
                .build();
    }

    @Nested
    @DisplayName("subscribe")
    class Subscribe {

        @Test
        @DisplayName("신규 endpoint면 그대로 저장한다")
        void subscribe_newEndpoint() {
            when(pushSubscriptionPort.existsByEndpoint(command.getEndpoint())).thenReturn(false);
            when(pushSubscriptionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.subscribe(command);

            ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
            verify(pushSubscriptionPort).save(captor.capture());
            verify(pushSubscriptionPort, never()).deleteByEndpoint(any());

            PushSubscription saved = captor.getValue();
            assertThat(saved.getEndpoint()).isEqualTo(command.getEndpoint());
            assertThat(saved.getTimezone()).isEqualTo("Asia/Seoul");
        }

        @Test
        @DisplayName("기존 endpoint가 있으면 삭제 후 재저장한다 (upsert)")
        void subscribe_existingEndpoint_upsert() {
            when(pushSubscriptionPort.existsByEndpoint(command.getEndpoint())).thenReturn(true);
            when(pushSubscriptionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.subscribe(command);

            verify(pushSubscriptionPort).deleteByEndpoint(command.getEndpoint());
            verify(pushSubscriptionPort).save(any());
        }

        @Test
        @DisplayName("timezone이 null이면 Asia/Seoul로 기본값 적용")
        void subscribe_nullTimezone_defaultsToSeoul() {
            SubscribePushCommand noTzCmd = SubscribePushCommand.builder()
                    .memberId(memberId)
                    .endpoint("https://push.example.com/ep2")
                    .p256dh("p256dh")
                    .auth("auth")
                    .timezone(null)
                    .build();
            when(pushSubscriptionPort.existsByEndpoint(any())).thenReturn(false);
            when(pushSubscriptionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.subscribe(noTzCmd);

            ArgumentCaptor<PushSubscription> captor = ArgumentCaptor.forClass(PushSubscription.class);
            verify(pushSubscriptionPort).save(captor.capture());
            assertThat(captor.getValue().getTimezone()).isEqualTo("Asia/Seoul");
        }

        @Test
        @DisplayName("잘못된 timezone이면 IllegalArgumentException을 던진다")
        void subscribe_invalidTimezone_throws() {
            SubscribePushCommand badTzCmd = SubscribePushCommand.builder()
                    .memberId(memberId)
                    .endpoint("https://push.example.com/ep3")
                    .p256dh("p256dh")
                    .auth("auth")
                    .timezone("Invalid/Zone")
                    .build();

            assertThatThrownBy(() -> service.subscribe(badTzCmd))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid timezone");
        }
    }

    @Nested
    @DisplayName("unsubscribe")
    class Unsubscribe {

        private static final String ENDPOINT = "https://push.example.com/endpoint";

        @Test
        @DisplayName("다른 기기 구독이 남아 있으면 endpoint만 삭제하고 토픽은 유지한다")
        void otherDeviceRemains_keepsTopics() {
            PushSubscription otherDevice = PushSubscription.of(memberId, "https://push.example.com/other", "p", "a", "ua", "Asia/Seoul");
            when(pushSubscriptionPort.findByMemberId(memberId)).thenReturn(List.of(otherDevice));

            service.unsubscribe(memberId, ENDPOINT);

            verify(pushSubscriptionPort).deleteByEndpoint(ENDPOINT);
            verify(pushSubscriptionTopicPort, never()).deleteAllByMemberId(any());
            verifyNoInteractions(pushTopicHistoryPort);
        }

        @Test
        @DisplayName("마지막 기기면 토픽을 모두 삭제하고 ALL_DEVICES_REMOVED 이력을 남긴다")
        void lastDevice_deletesTopicsAndRecordsHistory() {
            when(pushSubscriptionPort.findByMemberId(memberId)).thenReturn(List.of());
            when(pushSubscriptionTopicPort.findTopicsByMemberId(memberId)).thenReturn(List.of(PushTopic.PRAYER, PushTopic.READING));

            service.unsubscribe(memberId, ENDPOINT);

            verify(pushSubscriptionPort).deleteByEndpoint(ENDPOINT);
            verify(pushSubscriptionTopicPort).deleteAllByMemberId(memberId);

            ArgumentCaptor<PushTopicHistory> captor = ArgumentCaptor.forClass(PushTopicHistory.class);
            verify(pushTopicHistoryPort, times(2)).save(captor.capture());
            assertThat(captor.getAllValues())
                    .extracting(PushTopicHistory::getTopic)
                    .containsExactlyInAnyOrder(PushTopic.PRAYER, PushTopic.READING);
            assertThat(captor.getAllValues())
                    .allSatisfy(h -> {
                        assertThat(h.getAction()).isEqualTo(PushTopicAction.UNSUBSCRIBE);
                        assertThat(h.getSource()).isEqualTo(PushTopicChangeSource.ALL_DEVICES_REMOVED);
                    });
        }

        @Test
        @DisplayName("마지막 기기여도 구독 토픽이 없으면 이력을 남기지 않는다")
        void lastDevice_noTopics_noHistory() {
            when(pushSubscriptionPort.findByMemberId(memberId)).thenReturn(List.of());
            when(pushSubscriptionTopicPort.findTopicsByMemberId(memberId)).thenReturn(List.of());

            service.unsubscribe(memberId, ENDPOINT);

            verify(pushSubscriptionTopicPort, never()).deleteAllByMemberId(any());
            verifyNoInteractions(pushTopicHistoryPort);
        }
    }
}
