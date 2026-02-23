package mitl.IntoTheHeaven.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

@Getter
@SuperBuilder(toBuilder = true)
public class Event extends AggregateRoot<Event, EventId> {

    private final String entityId;
    private final EntityType entityType;
    private final String title;
    private final String description;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String location;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;
}
