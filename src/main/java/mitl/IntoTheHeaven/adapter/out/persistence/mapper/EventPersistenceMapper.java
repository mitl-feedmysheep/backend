package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.EventJpaEntity;
import mitl.IntoTheHeaven.domain.enums.EventColor;
import mitl.IntoTheHeaven.domain.model.Event;
import mitl.IntoTheHeaven.domain.model.EventId;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class EventPersistenceMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Event toDomain(EventJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Event.builder()
                .id(EventId.from(entity.getId()))
                .entityId(entity.getEntityId())
                .entityType(entity.getEntityType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .startTime(parseTime(entity.getStartTime()))
                .endTime(parseTime(entity.getEndTime()))
                .location(entity.getLocation())
                .color(parseColor(entity.getColor()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public EventJpaEntity toEntity(Event domain) {
        if (domain == null) {
            return null;
        }
        return EventJpaEntity.builder()
                .id(domain.getId().getValue())
                .entityId(domain.getEntityId())
                .entityType(domain.getEntityType())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .startTime(formatTime(domain.getStartTime()))
                .endTime(formatTime(domain.getEndTime()))
                .location(domain.getLocation())
                .color(domain.getColor() != null ? domain.getColor().name() : null)
                .build();
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return null;
        }
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    private String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : null;
    }

    private EventColor parseColor(String color) {
        if (color == null || color.isBlank()) {
            return null;
        }
        return EventColor.valueOf(color);
    }
}
