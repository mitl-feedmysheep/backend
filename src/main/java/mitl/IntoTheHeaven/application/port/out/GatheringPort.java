package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Gathering;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GatheringPort {

    List<Gathering> findAllByGroupId(UUID groupId);

    Optional<Gathering> findDetailById(UUID gatheringId);
} 