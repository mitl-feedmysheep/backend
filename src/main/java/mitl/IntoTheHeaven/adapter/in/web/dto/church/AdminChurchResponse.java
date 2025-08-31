package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Church;

@Getter
public class AdminChurchResponse {
  private final UUID id;
  private final String name;
  private final String location;
  private final String number;
  private final String homepageUrl;
  private final String description;

  @Builder
  public AdminChurchResponse(UUID id, String name, String location, String number, String homepageUrl,
      String description) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.number = number;
    this.homepageUrl = homepageUrl;
    this.description = description;
  }

  public static AdminChurchResponse from(Church church) {
    return AdminChurchResponse.builder()
        .id(church.getId().getValue())
        .name(church.getName())
        .location(church.getLocation())
        .number(church.getNumber())
        .homepageUrl(church.getHomepageUrl())
        .description(church.getDescription())
        .build();
  }

  public static List<AdminChurchResponse> from(List<Church> churches) {
    return churches.stream().map(AdminChurchResponse::from).collect(Collectors.toList());
  }
}
