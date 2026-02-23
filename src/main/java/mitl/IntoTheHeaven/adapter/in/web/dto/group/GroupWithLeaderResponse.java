package mitl.IntoTheHeaven.adapter.in.web.dto.group;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.application.dto.GroupWithLeader;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GroupWithLeaderResponse {

    private final UUID groupId;
    private final String groupName;
    private final String leaderName;

    public static GroupWithLeaderResponse from(GroupWithLeader domain) {
        return GroupWithLeaderResponse.builder()
                .groupId(domain.getGroupId())
                .groupName(domain.getGroupName())
                .leaderName(domain.getLeaderName())
                .build();
    }

    public static List<GroupWithLeaderResponse> from(List<GroupWithLeader> domains) {
        return domains.stream()
                .map(GroupWithLeaderResponse::from)
                .toList();
    }
}
