package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;

import java.util.List;

@Builder
public record VisitMemberCommand(
        ChurchMemberId churchMemberId,
        String story,
        List<PrayerCommand> prayers
) {
}

