package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

        String notes
) {
        public static CreateVisitCommand toCommand(AdminCreateVisitRequest request, ChurchId churchId, MemberId memberId) {
                return CreateVisitCommand.builder()
                        .churchId(churchId)
                        .pastorMemberId(memberId)
                        .date(request.date)
                        .startedAt(request.startedAt)
                        .endedAt(request.endedAt)
                        .place(request.place)
                        .expense(request.expense)
                        .notes(request.notes)
                        .build();
        }
}

