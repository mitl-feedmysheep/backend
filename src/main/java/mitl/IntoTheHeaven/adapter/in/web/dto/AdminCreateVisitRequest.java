package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record AdminCreateVisitRequest(
        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Started at is required")
        LocalDateTime startedAt,

        @NotNull(message = "Ended at is required")
        LocalDateTime endedAt,

        @NotBlank(message = "Place is required")
        String place,

        @NotNull(message = "Expense is required")
        Integer expense,

        String notes,

        @Valid
        @NotEmpty(message = "Visit members are required")
        List<VisitMemberRequest> visitMembers
) {
    public static CreateVisitCommand toCommand(AdminCreateVisitRequest request, ChurchId churchId, ChurchMemberId pastorChurchMemberId) {
        return CreateVisitCommand.builder()
                .churchId(churchId)
                .pastorChurchMemberId(pastorChurchMemberId)
                .date(request.date)
                .startedAt(request.startedAt)
                .endedAt(request.endedAt)
                .place(request.place)
                .expense(request.expense)
                .notes(request.notes)
                .visitMembers(request.visitMembers.stream()
                        .map(VisitMemberRequest::toCommand)
                        .collect(Collectors.toList()))
                .build();
    }
}

