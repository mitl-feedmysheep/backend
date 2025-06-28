package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface GroupQueryUseCase {
    List<Group> getGroupsByMemberId(MemberId memberId);
} 