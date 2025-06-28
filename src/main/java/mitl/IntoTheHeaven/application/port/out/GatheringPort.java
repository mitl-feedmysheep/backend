package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GroupId;

import java.util.List;

public interface GatheringPort {
    List<Gathering> findAllByGroupId(GroupId groupId);
} 