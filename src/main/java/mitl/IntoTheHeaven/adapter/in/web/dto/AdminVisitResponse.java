package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.Visit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record AdminVisitResponse(
        UUID id,
        UUID churchId,
        LocalDate date,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String place,
        Integer expense,
        String notes,
        List<VisitMemberResponse> visitMembers,
        int photoCount,
        LocalDateTime createdAt
) {
    public static AdminVisitResponse from(Visit visit) {
        return AdminVisitResponse.builder()
                .id(visit.getId().getValue())
                .churchId(visit.getChurchId().getValue())
                .date(visit.getDate())
                .startedAt(visit.getStartedAt())
                .endedAt(visit.getEndedAt())
                .place(visit.getPlace())
                .expense(visit.getExpense())
                .notes(visit.getNotes())
                .visitMembers(visit.getVisitMembers().stream()
                        .map(VisitMemberResponse::from)
                        .collect(Collectors.toList()))
                .photoCount(visit.getPhotoCount())
                .createdAt(visit.getCreatedAt())
                .build();
    }
}

