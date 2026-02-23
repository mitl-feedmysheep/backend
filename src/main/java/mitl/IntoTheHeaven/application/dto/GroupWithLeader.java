package mitl.IntoTheHeaven.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GroupWithLeader {

    private final UUID groupId;
    private final String groupName;
    private final String leaderName;
}
