package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.visit.UpdateVisitMemberRequest;
import mitl.IntoTheHeaven.domain.model.VisitId;
import mitl.IntoTheHeaven.domain.model.VisitMemberId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class UpdateVisitMemberCommand {

  private final VisitId visitId;
  private final VisitMemberId visitMemberId;
  private final String story;
  private final List<PrayerUpdateCommand> prayers;

  public static UpdateVisitMemberCommand from(VisitId visitId, VisitMemberId visitMemberId,
      UpdateVisitMemberRequest request) {
    List<PrayerUpdateCommand> prayerCommands = request.getPrayers() != null
        ? request.getPrayers().stream()
            .map(PrayerUpdateCommand::from)
            .collect(Collectors.toList())
        : List.of();

    return new UpdateVisitMemberCommand(
        visitId,
        visitMemberId,
        request.getStory() != null ? request.getStory() : "",
        prayerCommands);
  }

  @Getter
  @RequiredArgsConstructor
  public static class PrayerUpdateCommand {
    private final UUID id; // optional existing id
    private final String prayerRequest;
    private final String description;

    public static PrayerUpdateCommand from(UpdateVisitMemberRequest.PrayerRequest request) {
      return new PrayerUpdateCommand(
          request.getId(),
          request.getPrayerRequest(),
          request.getDescription());
    }
  }
}
