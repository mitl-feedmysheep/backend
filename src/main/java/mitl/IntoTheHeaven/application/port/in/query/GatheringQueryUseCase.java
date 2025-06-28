package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;

import java.util.List;

public interface GatheringQueryUseCase {

    List<Gathering> getGatheringsByGroupId(GroupId groupId);

    Gathering getGatheringDetail(GatheringId gatheringId);
} 