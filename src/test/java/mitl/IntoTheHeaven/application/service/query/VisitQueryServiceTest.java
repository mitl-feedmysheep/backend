package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitQueryServiceTest {

    @Mock
    private VisitPort visitPort;

    @InjectMocks
    private VisitQueryService visitQueryService;

    private Visit createVisit(VisitId id, ChurchId churchId, LocalDate date, LocalDateTime startedAt) {
        return Visit.builder()
                .id(id)
                .churchId(churchId)
                .pastorMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .date(date)
                .startedAt(startedAt)
                .place("교회 사무실")
                .build();
    }

    @Nested
    @DisplayName("getAllMyVisits")
    class GetAllMyVisits {

        @Test
        @DisplayName("심방 목록을 날짜 내림차순, 시작시간 내림차순으로 정렬")
        void shouldReturnVisitsSortedByDateAndStartedAtDesc() {
            ChurchId churchId = ChurchId.from(UUID.randomUUID());
            MemberId memberId = MemberId.from(UUID.randomUUID());

            Visit v1 = createVisit(
                    VisitId.from(UUID.randomUUID()), churchId,
                    LocalDate.of(2025, 1, 10),
                    LocalDateTime.of(2025, 1, 10, 9, 0));
            Visit v2 = createVisit(
                    VisitId.from(UUID.randomUUID()), churchId,
                    LocalDate.of(2025, 3, 5),
                    LocalDateTime.of(2025, 3, 5, 14, 0));
            Visit v3 = createVisit(
                    VisitId.from(UUID.randomUUID()), churchId,
                    LocalDate.of(2025, 3, 5),
                    LocalDateTime.of(2025, 3, 5, 10, 0));

            when(visitPort.findAllByChurchIdAndMemberId(churchId, memberId))
                    .thenReturn(List.of(v1, v2, v3));

            List<Visit> result = visitQueryService.getAllMyVisits(churchId, memberId);

            assertThat(result).hasSize(3);
            // 2025-03-05 14:00 first
            assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2025, 3, 5));
            assertThat(result.get(0).getStartedAt()).isEqualTo(LocalDateTime.of(2025, 3, 5, 14, 0));
            // 2025-03-05 10:00 second
            assertThat(result.get(1).getStartedAt()).isEqualTo(LocalDateTime.of(2025, 3, 5, 10, 0));
            // 2025-01-10 last
            assertThat(result.get(2).getDate()).isEqualTo(LocalDate.of(2025, 1, 10));
        }

        @Test
        @DisplayName("심방이 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoVisits() {
            ChurchId churchId = ChurchId.from(UUID.randomUUID());
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(visitPort.findAllByChurchIdAndMemberId(churchId, memberId))
                    .thenReturn(List.of());

            List<Visit> result = visitQueryService.getAllMyVisits(churchId, memberId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getVisitById")
    class GetVisitById {

        @Test
        @DisplayName("심방 조회 성공 - 교회 소속 확인 통과")
        void shouldReturnVisitWhenChurchMatches() {
            ChurchId churchId = ChurchId.from(UUID.randomUUID());
            VisitId visitId = VisitId.from(UUID.randomUUID());

            Visit visit = createVisit(visitId, churchId, LocalDate.now(), LocalDateTime.now());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visit));

            Visit result = visitQueryService.getVisitById(visitId, churchId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(visitId);
            verify(visitPort).findDetailById(visitId);
        }

        @Test
        @DisplayName("존재하지 않는 심방 조회 시 IllegalArgumentException 발생")
        void shouldThrowExceptionWhenVisitNotFound() {
            VisitId visitId = VisitId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> visitQueryService.getVisitById(visitId, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Visit not found:");
        }

        @Test
        @DisplayName("다른 교회의 심방 접근 시 IllegalArgumentException 발생")
        void shouldThrowExceptionWhenChurchMismatch() {
            ChurchId visitChurchId = ChurchId.from(UUID.randomUUID());
            ChurchId requestChurchId = ChurchId.from(UUID.randomUUID());
            VisitId visitId = VisitId.from(UUID.randomUUID());

            Visit visit = createVisit(visitId, visitChurchId, LocalDate.now(), LocalDateTime.now());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visit));

            assertThatThrownBy(() -> visitQueryService.getVisitById(visitId, requestChurchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Access denied: Visit does not belong to your church");
        }
    }

    @Nested
    @DisplayName("getMyVisits")
    class GetMyVisits {

        @Test
        @DisplayName("내 심방 목록을 날짜 내림차순으로 정렬")
        void shouldReturnVisitsSortedByDateDesc() {
            ChurchMemberId churchMemberId = ChurchMemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            Visit older = createVisit(
                    VisitId.from(UUID.randomUUID()), churchId,
                    LocalDate.of(2025, 1, 1),
                    LocalDateTime.of(2025, 1, 1, 10, 0));
            Visit newer = createVisit(
                    VisitId.from(UUID.randomUUID()), churchId,
                    LocalDate.of(2025, 6, 15),
                    LocalDateTime.of(2025, 6, 15, 14, 0));

            when(visitPort.findMyVisits(churchMemberId)).thenReturn(List.of(older, newer));

            List<Visit> result = visitQueryService.getMyVisits(churchMemberId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2025, 6, 15));
            assertThat(result.get(1).getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        }

        @Test
        @DisplayName("심방이 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoVisits() {
            ChurchMemberId churchMemberId = ChurchMemberId.from(UUID.randomUUID());

            when(visitPort.findMyVisits(churchMemberId)).thenReturn(List.of());

            List<Visit> result = visitQueryService.getMyVisits(churchMemberId);

            assertThat(result).isEmpty();
        }
    }
}
