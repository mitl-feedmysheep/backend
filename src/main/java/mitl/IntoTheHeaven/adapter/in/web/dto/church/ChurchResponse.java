package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Church;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ChurchResponse {
    private final UUID id;
    private final String name;
    private final String location;
    private final String number;
    private final String homepageUrl;
    private final String description;
    private final LocalDateTime createdAt;

    @Builder
    public ChurchResponse(UUID id, String name, String location, String number, String homepageUrl, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.number = number;
        this.homepageUrl = homepageUrl;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static ChurchResponse from(Church church) {
        return ChurchResponse.builder()
                .id(church.getId().getValue())
                .name(church.getName())
                .location(church.getLocation())
                .number(church.getNumber())
                .homepageUrl(church.getHomepageUrl())
                .description(church.getDescription())
                .createdAt(church.getCreatedAt())
                .build();
    }

    public static List<ChurchResponse> from(List<Church> churches) {
        return churches.stream()
                .map(ChurchResponse::from)
                .collect(Collectors.toList());
    }
}