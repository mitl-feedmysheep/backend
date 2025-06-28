package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Group;

import java.util.List;
import java.util.UUID;

public interface GroupQueryUseCase {
    List<Group> getGroupsByMemberId(UUID memberId);
} 