package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.EventPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.EventJpaRepository;
import mitl.IntoTheHeaven.application.port.out.EventPort;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventPersistenceAdapter implements EventPort {

    private final EventJpaRepository eventJpaRepository;
    private final EventPersistenceMapper eventPersistenceMapper;

    @Override
    public List<Event> findByEntityIdAndMonth(String entityId, EntityType entityType, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return eventJpaRepository
                .findAllByEntityIdAndEntityTypeAndDateBetweenOrderByDateAscStartTimeAsc(
                        entityId, entityType, startDate, endDate)
                .stream()
                .map(eventPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Event save(Event event) {
        return eventPersistenceMapper.toDomain(
                eventJpaRepository.save(eventPersistenceMapper.toEntity(event)));
    }
}
