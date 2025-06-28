package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Gathering;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GatheringResponse {

    private final String id;
    private final String name;
    private final LocalDate date;
    private final String place;

    @Builder
    public GatheringResponse(String id, String name, LocalDate date, String place) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.place = place;
    }

    public static GatheringResponse from(Gathering gathering) {
        return GatheringResponse.builder()
                .id(gathering.getId().getValue().toString())
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