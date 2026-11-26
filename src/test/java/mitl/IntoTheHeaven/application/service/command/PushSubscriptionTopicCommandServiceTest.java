package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.PushTopicHistoryPort;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.enums.PushTopicAction;
import mitl.IntoTheHeaven.domain.enums.PushTopicChangeSource;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushTopicHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushSubscriptionTopicCommandService")
class PushSubscriptionTopicCommandServiceTest {

    @Mock
    private PushSubscriptionTopicPort pushSubscriptionTopicPort;

    @Mock
    private PushTopicHistoryPort pushTopicHistoryPort;

    @InjectMocks
    private PushSubscriptionTopicCommandService service;

    private final MemberId memberId = MemberId.from(UUID.randomUUID());

    @Nested
    @DisplayName("subscribe")
    class Subscribe {

        @Test
        @DisplayName("꺼져 있던 토픽을 켜면 구독하고 SUBSCRIBE 이력을 남긴다")
        void off_to_on_recordsHistory() {
            when(pushSubscriptionTopicPort.isSubscribed(memberId, PushTopic.PRAYER)).thenReturn(false);

            service.subscribe(memberId, PushTopic.PRAYER);

            verify(pushSubscriptionTopicPort).subscribe(memberId, PushTopic.PRAYER);
            ArgumentCaptor<PushTopicHistory> captor = ArgumentCaptor.forClass(PushTopicHistory.class);
            verify(pushTopicHistoryPort).save(captor.capture());
            assertThat(captor.getValue().getMemberId()).isEqualTo(memberId);
            assertThat(captor.getValue().getTopic()).isEqualTo(PushTopic.PRAYER);
            assertThat(captor.getValue().getAction()).isEqualTo(PushTopicAction.SUBSCRIBE);
            assertThat(captor.getValue().getSource()).isEqualTo(PushTopicChangeSource.USER_TOGGLE);
        }

        @Test
        @DisplayName("이미 켜져 있으면 아무것도 기록하지 않는다")
        void alreadyOn_noop() {
            when(pushSubscriptionTopicPort.isSubscribed(memberId, PushTopic.PRAYER)).thenReturn(true);

            service.subscribe(memberId, PushTopic.PRAYER);

            verify(pushSubscriptionTopicPort, never()).subscribe(any(), any());
            verifyNoInteractions(pushTopicHistoryPort);
        }
    }

    @Nested
    @DisplayName("unsubscribe")
    class Unsubscribe {

        @Test
        @DisplayName("켜져 있던 토픽을 끄면 해지하고 UNSUBSCRIBE 이력을 남긴다")
        void on_to_off_recordsHistory() {
            when(pushSubscriptionTopicPort.isSubscribed(memberId, PushTopic.READING)).thenReturn(true);

            service.unsubscribe(memberId, PushTopic.READING);

            verify(pushSubscriptionTopicPort).unsubscribe(memberId, PushTopic.READING);
            ArgumentCaptor<PushTopicHistory> captor = ArgumentCaptor.forClass(PushTopicHistory.class);
            verify(pushTopicHistoryPort).save(captor.capture());
            assertThat(captor.getValue().getAction()).isEqualTo(PushTopicAction.UNSUBSCRIBE);
            assertThat(captor.getValue().getSource()).isEqualTo(PushTopicChangeSource.USER_TOGGLE);
        }

        @Test
        @DisplayName("이미 꺼져 있으면 아무것도 기록하지 않는다")
        void alreadyOff_noop() {
            when(pushSubscriptionTopicPort.isSubscribed(memberId, PushTopic.READING)).thenReturn(false);

            service.unsubscribe(memberId, PushTopic.READING);

            verify(pushSubscriptionTopicPort, never()).unsubscribe(any(), any());
            verifyNoInteractions(pushTopicHistoryPort);
        }
    }
}
