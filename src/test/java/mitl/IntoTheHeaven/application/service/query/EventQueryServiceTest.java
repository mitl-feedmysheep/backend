package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.EventPort;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;
import mitl.IntoTheHeaven.domain.model.EventId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {

    @Mock
    private EventPort eventPort;

    @InjectMocks
    private EventQueryService eventQueryService;

    @Nested
    @DisplayName("getEventsByEntityIdAndMonth")
    class GetEventsByEntityIdAndMonth {

        @Test
        @DisplayName("엔티티 ID와 월로 이벤트 목록 조회")
        void shouldReturnEventsForEntityAndMonth() {
            String entityId = UUID.randomUUID().toString();
            EntityType entityType = EntityType.CHURCH;
            int year = 2025;
            int month = 6;

            List<Event> events = List.of(
                    Event.builder()
                            .id(EventId.from(UUID.randomUUID()))
                            .entityId(entityId)
                            .entityType(entityType)
                            .title("여름 수련회")
                            .startDate(LocalDate.of(2025, 6, 15))
                            .endDate(LocalDate.of(2025, 6, 17))
                            .build(),
                    Event.builder()
                            .id(EventId.from(UUID.randomUUID()))
                            .entityId(entityId)
                            .entityType(entityType)
                            .title("셀 리더 모임")
                            .startDate(LocalDate.of(2025, 6, 20))
                            .endDate(LocalDate.of(2025, 6, 20))
                            .build()
            );

            when(eventPort.findByEntityIdAndMonth(entityId, entityType, year, month))
                    .thenReturn(events);

            List<Event> result = eventQueryService
                    .getEventsByEntityIdAndMonth(entityId, entityType, year, month);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("여름 수련회");
            assertThat(result.get(1).getTitle()).isEqualTo("셀 리더 모임");
            verify(eventPort).findByEntityIdAndMonth(entityId, entityType, year, month);
        }

        @Test
        @DisplayName("해당 월에 이벤트가 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoEvents() {
            String entityId = UUID.randomUUID().toString();
            EntityType entityType = EntityType.GROUP;
            int year = 2025;
            int month = 12;

            when(eventPort.findByEntityIdAndMonth(entityId, entityType, year, month))
                    .thenReturn(List.of());

            List<Event> result = eventQueryService
                    .getEventsByEntityIdAndMonth(entityId, entityType, year, month);

            assertThat(result).isEmpty();
        }
    }
}
