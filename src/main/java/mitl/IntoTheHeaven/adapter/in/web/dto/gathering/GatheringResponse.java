package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Gathering;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class GatheringResponse {

    private final UUID id;
    private final String name;
    private final LocalDate date;
    private final String place;

    public static GatheringResponse from(Gathering gathering) {
        return GatheringResponse.builder()
                .id(gathering.getId().getValue())
                .name(gathering.getName())
                .date(gathering.getDate())
                .place(gathering.getPlace())
                .build();
    }

    public static List<GatheringResponse> from(List<Gathering> gatherings) {
        return gatherings.stream()
                .map(GatheringResponse::from)
                .collect(Collectors.toList());
    }
}