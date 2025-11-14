package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import mitl.IntoTheHeaven.application.port.in.command.dto.VisitMemberCommand;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record VisitMemberRequest(
        @NotNull(message = "Church member ID is required")
        UUID churchMemberId,

        String story,

        @Valid
        List<PrayerRequest> prayers
) {
    public static VisitMemberCommand toCommand(VisitMemberRequest request) {
        return VisitMemberCommand.builder()
                .churchMemberId(ChurchMemberId.from(request.churchMemberId))
                .story(request.story)
                .prayers(request.prayers != null
                        ? request.prayers.stream()
                                .map(PrayerRequest::toCommand)
                                .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}

