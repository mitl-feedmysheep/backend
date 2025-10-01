package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreateVisitCommand(
        ChurchId churchId,
        ChurchMemberId pastorChurchMemberId,
        LocalDate date,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String place,
        Integer expense,
        String notes,
        List<VisitMemberCommand> visitMembers
) {
}

