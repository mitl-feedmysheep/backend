package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;

import java.util.List;

public interface EventQueryUseCase {

    List<Event> getEventsByEntityIdAndMonth(String entityId, EntityType entityType, int year, int month);
}
