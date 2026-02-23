package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;

import java.util.List;

public interface EventPort {

    List<Event> findByEntityIdAndMonth(String entityId, EntityType entityType, int year, int month);

    Event save(Event event);
}
