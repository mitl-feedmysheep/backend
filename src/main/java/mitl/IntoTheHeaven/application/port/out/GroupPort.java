package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Group;

import java.util.List;
import java.util.UUID;

public interface GroupPort {
    List<Group> findGroupsByMemberId(UUID memberId);
} 