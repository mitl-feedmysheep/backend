package mitl.IntoTheHeaven.adapter.in.web.dto.visit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.PrayerResponse;
import mitl.IntoTheHeaven.domain.model.VisitMember;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class UpdateVisitMemberResponse {

    private final UUID id;
    private final String story;
    private final List<PrayerResponse> prayers;

    public static UpdateVisitMemberResponse from(VisitMember visitMember) {
        return new UpdateVisitMemberResponse(
            visitMember.getId().getValue(),
            visitMember.getStory(),
            visitMember.getPrayers().stream()
                .map(PrayerResponse::from)
                .collect(Collectors.toList())
        );
    }
}


