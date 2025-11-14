package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

@Builder
public record AddVisitMembersCommand(
                List<MemberId> memberIds) {
}
