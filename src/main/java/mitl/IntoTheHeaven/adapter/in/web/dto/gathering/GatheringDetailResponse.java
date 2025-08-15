package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import mitl.IntoTheHeaven.domain.model.Gathering;

@Data
@Builder
public class GatheringDetailResponse {

    private UUID id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String place;
    private String leaderComment;
    private String adminComment;
    private List<GatheringMemberResponse> gatheringMembers;

    public static GatheringDetailResponse from(Gathering gathering) {
        return GatheringDetailResponse.builder()
                .id(gathering.getId().getValue())
                .name(gathering.getName())
                .description(gathering.getDescription())
                .date(gathering.getDate())
                .startedAt(gathering.getStartedAt())
                .endedAt(gathering.getEndedAt())
                .place(gathering.getPlace())
                .leaderComment(gathering.getLeaderComment())
                .adminComment(gathering.getAdminComment())
                .gatheringMembers(gathering.getGatheringMembers().stream()
                        .map(GatheringMemberResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
} 