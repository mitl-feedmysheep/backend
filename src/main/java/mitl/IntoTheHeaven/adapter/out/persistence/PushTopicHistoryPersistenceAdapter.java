package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushTopicHistoryJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.PushTopicHistoryJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushTopicHistoryPort;
import mitl.IntoTheHeaven.domain.model.PushTopicHistory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushTopicHistoryPersistenceAdapter implements PushTopicHistoryPort {

    private final PushTopicHistoryJpaRepository repository;

    @Override
    public void save(PushTopicHistory history) {
        repository.save(PushTopicHistoryJpaEntity.builder()
                .id(history.getId())
                .memberId(history.getMemberId().getValue())
                .topic(history.getTopic())
                .action(history.getAction())
                .source(history.getSource())
                .build());
    }
}
