package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrayerQueryServiceTest {

    @Mock
    private ChurchPort churchPort;

    @Mock
    private PrayerPort prayerPort;

    @InjectMocks
    private PrayerQueryService prayerQueryService;

    @Nested
    @DisplayName("getPrayerRequestCountByMemberIdAndChurchId")
    class GetPrayerRequestCount {

        @Test
        @DisplayName("교회가 존재하면 기도제목 수를 반환")
        void shouldReturnCountWhenChurchExists() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            Church church = Church.builder()
                    .id(churchId)
                    .name("테스트교회")
                    .build();

            when(churchPort.findById(churchId.getValue())).thenReturn(church);
            when(prayerPort.findPrayerRequestCountByChurchId(churchId.getValue())).thenReturn(15L);

            Long result = prayerQueryService
                    .getPrayerRequestCountByMemberIdAndChurchId(memberId, churchId);

            assertThat(result).isEqualTo(15L);
            verify(churchPort).findById(churchId.getValue());
            verify(prayerPort).findPrayerRequestCountByChurchId(churchId.getValue());
        }

        @Test
        @DisplayName("교회가 존재하지 않으면 RuntimeException 발생")
        void shouldThrowExceptionWhenChurchNotFound() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            when(churchPort.findById(churchId.getValue())).thenReturn(null);

            assertThatThrownBy(() -> prayerQueryService
                    .getPrayerRequestCountByMemberIdAndChurchId(memberId, churchId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("권한이 없어요 :(");
        }

        @Test
        @DisplayName("기도제목이 없으면 0을 반환")
        void shouldReturnZeroWhenNoPrayers() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            Church church = Church.builder()
                    .id(churchId)
                    .name("테스트교회")
                    .build();

            when(churchPort.findById(churchId.getValue())).thenReturn(church);
            when(prayerPort.findPrayerRequestCountByChurchId(churchId.getValue())).thenReturn(0L);

            Long result = prayerQueryService
                    .getPrayerRequestCountByMemberIdAndChurchId(memberId, churchId);

            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("getMyPrayers")
    class GetMyPrayers {

        @Test
        @DisplayName("멤버의 기도제목 목록 조회")
        void shouldReturnMyPrayers() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            List<Prayer> prayers = List.of(
                    Prayer.builder()
                            .id(PrayerId.from(UUID.randomUUID()))
                            .memberId(memberId)
                            .prayerRequest("첫 번째 기도제목")
                            .isAnswered(false)
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Prayer.builder()
                            .id(PrayerId.from(UUID.randomUUID()))
                            .memberId(memberId)
                            .prayerRequest("두 번째 기도제목")
                            .isAnswered(true)
                            .createdAt(LocalDateTime.now().minusDays(7))
                            .build()
            );

            when(prayerPort.findAllByMemberId(memberId.getValue())).thenReturn(prayers);

            List<Prayer> result = prayerQueryService.getMyPrayers(memberId);

            assertThat(result).hasSize(2);
            verify(prayerPort).findAllByMemberId(memberId.getValue());
        }

        @Test
        @DisplayName("기도제목이 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoPrayers() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(prayerPort.findAllByMemberId(memberId.getValue())).thenReturn(List.of());

            List<Prayer> result = prayerQueryService.getMyPrayers(memberId);

            assertThat(result).isEmpty();
        }
    }
}
