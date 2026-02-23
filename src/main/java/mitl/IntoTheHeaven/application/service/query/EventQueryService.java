package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.EventQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.EventPort;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventQueryService implements EventQueryUseCase {

    private final EventPort eventPort;

    @Override
    public List<Event> getEventsByEntityIdAndMonth(String entityId, EntityType entityType, int year, int month) {
        return eventPort.findByEntityIdAndMonth(entityId, entityType, year, month);
    }
}
