package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.event.EventResponse;
import mitl.IntoTheHeaven.application.port.in.query.EventQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Event", description = "APIs for Calendar Event Management")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventQueryUseCase eventQueryUseCase;

    @Operation(summary = "Get Events by Month", description = "Retrieves events for a specific entity (church/group) in a given year and month.")
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEventsByMonth(
            @RequestParam("entityId") String entityId,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        List<Event> events = eventQueryUseCase.getEventsByEntityIdAndMonth(entityId, entityType, year, month);
        return ResponseEntity.ok(EventResponse.from(events));
    }
}
