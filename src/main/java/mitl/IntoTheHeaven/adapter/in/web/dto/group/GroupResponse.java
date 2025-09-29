package mitl.IntoTheHeaven.adapter.in.web.dto.group;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Group;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class GroupResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final UUID churchId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer groupMemberCount; // renamed
    private final String imageUrl;

    @Builder
    public GroupResponse(UUID id, String name, String description, UUID churchId, LocalDate startDate,
            LocalDate endDate, Integer groupMemberCount, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.churchId = churchId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupMemberCount = groupMemberCount;
        this.imageUrl = imageUrl;
    }

    public static GroupResponse from(Group group) {
        return GroupResponse.builder()
                .id(group.getId().getValue())
                .name(group.getName())
                .description(group.getDescription())
                .churchId(group.getChurchId().getValue())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .imageUrl(group.getMainImageUrl().orElse(null))
                .build();
    }

    public static GroupResponse from(Group group, int groupMemberCount) {
        return GroupResponse.builder()
                .id(group.getId().getValue())
                .name(group.getName())
                .description(group.getDescription())
                .churchId(group.getChurchId().getValue())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .groupMemberCount(groupMemberCount)
                .imageUrl(group.getMainImageUrl().orElse(null))
                .build();
    }

    public static List<GroupResponse> from(List<Group> groups) {
        return groups.stream()
                .map(GroupResponse::from)
                .collect(Collectors.toList());
    }
}