package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record AdminAddVisitMembersRequest(
                @NotEmpty(message = "Member IDs are required") List<@NotNull(message = "Member ID cannot be null") UUID> memberIds) {
        public static AddVisitMembersCommand toCommand(AdminAddVisitMembersRequest request) {
                return AddVisitMembersCommand.builder()
                                .memberIds(request.memberIds.stream()
                                                .map(MemberId::from)
                                                .collect(Collectors.toList()))
                                .build();
        }
}
