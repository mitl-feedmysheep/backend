package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;

import java.util.List;
import java.util.UUID;

public interface GroupQueryUseCase {
    List<GroupResponse> getGroupsByMemberId(UUID memberId);
} 