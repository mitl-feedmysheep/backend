package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrayerCommandServiceTest {

    @Mock
    private PrayerPort prayerPort;

    @InjectMocks
    private PrayerCommandService prayerCommandService;

    private UUID prayerUuid;
    private PrayerId prayerId;
    private Prayer existingPrayer;

    @BeforeEach
    void setUp() {
        prayerUuid = UUID.randomUUID();
        prayerId = PrayerId.from(prayerUuid);
        existingPrayer = Prayer.builder()
                .id(prayerId)
                .prayerRequest("기도제목")
                .description("설명")
                .isAnswered(false)
                .build();
    }

    @Nested
    @DisplayName("delete - 기도제목 삭제")
    class DeleteTests {

        @Test
        @DisplayName("기도제목을 soft delete하고 저장한다")
        void shouldSoftDeletePrayer() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(existingPrayer));
            when(prayerPort.save(any(Prayer.class))).thenAnswer(inv -> inv.getArgument(0));

            prayerCommandService.delete(prayerId);

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("기도제목이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenPrayerNotFound() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prayerCommandService.delete(prayerId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Prayer not found");

            verify(prayerPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateAnswered - 기도 응답 상태 변경")
    class UpdateAnsweredTests {

        @Test
        @DisplayName("isAnswered를 true로 변경한다")
        void shouldMarkAsAnswered() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(existingPrayer));
            when(prayerPort.save(any(Prayer.class))).thenAnswer(inv -> inv.getArgument(0));

            prayerCommandService.updateAnswered(prayerId, true);

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            assertThat(captor.getValue().isAnswered()).isTrue();
        }

        @Test
        @DisplayName("isAnswered를 false로 변경한다")
        void shouldMarkAsUnanswered() {
            Prayer answeredPrayer = existingPrayer.markAnswered(true);
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(answeredPrayer));
            when(prayerPort.save(any(Prayer.class))).thenAnswer(inv -> inv.getArgument(0));

            prayerCommandService.updateAnswered(prayerId, false);

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            assertThat(captor.getValue().isAnswered()).isFalse();
        }

        @Test
        @DisplayName("기도제목이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenPrayerNotFound() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prayerCommandService.updateAnswered(prayerId, true))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Prayer not found");

            verify(prayerPort, never()).save(any());
        }
    }
}
