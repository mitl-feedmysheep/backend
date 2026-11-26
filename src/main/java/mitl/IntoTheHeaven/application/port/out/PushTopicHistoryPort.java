package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.PushTopicHistory;

public interface PushTopicHistoryPort {

    void save(PushTopicHistory history);
}
