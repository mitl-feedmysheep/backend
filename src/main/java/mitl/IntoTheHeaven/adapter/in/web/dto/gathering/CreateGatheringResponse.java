package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Gathering;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateGatheringResponse {

    private final UUID id;
    private final String name;
    private final String description;
    private final LocalDate date;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final String place;

    public static CreateGatheringResponse from(Gathering gathering) {
        return CreateGatheringResponse.builder()
                .id(gathering.getId().getValue())
                .name(gathering.getName())
                .description(gathering.getDescription())
                .date(gathering.getDate())
                .startedAt(gathering.getStartedAt())
                .endedAt(gathering.getEndedAt())
                .place(gathering.getPlace())
                .build();
    }
} 