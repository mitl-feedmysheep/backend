package mitl.IntoTheHeaven.adapter.in.web.dto.event;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class EventResponse {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final UUID id;
    private final String title;
    private final String description;
    private final LocalDate date;
    private final String startTime;
    private final String endTime;
    private final String location;

    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId().getValue())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .startTime(formatTime(event.getStartTime()))
                .endTime(formatTime(event.getEndTime()))
                .location(event.getLocation())
                .build();
    }

    private static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMAT) : null;
    }

    public static List<EventResponse> from(List<Event> events) {
        return events.stream()
                .map(EventResponse::from)
                .toList();
    }
}
