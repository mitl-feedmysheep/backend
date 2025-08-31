package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.application.port.in.query.dto.GatheringWithStatistics;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;

import java.util.List;

public interface GatheringQueryUseCase {

    Gathering getGatheringDetail(GatheringId gatheringId);

    List<GatheringWithStatistics> getGatheringsWithStatisticsByGroupId(GroupId groupId);
}