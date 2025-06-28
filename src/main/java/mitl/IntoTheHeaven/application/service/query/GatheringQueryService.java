package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GroupId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringQueryService implements GatheringQueryUseCase {

    private final GatheringPort gatheringPort;

    @Override
    public List<Gathering> getGatheringsByGroupId(GroupId groupId) {
        return gatheringPort.findAllByGroupId(groupId);
    }
} 