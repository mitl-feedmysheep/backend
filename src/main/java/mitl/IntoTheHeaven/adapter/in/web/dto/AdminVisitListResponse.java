package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.Visit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record AdminVisitListResponse(
        UUID id,
        LocalDate date,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String place,
        Integer expense,
        int memberCount,
        int photoCount,
        LocalDateTime createdAt
) {
    public static AdminVisitListResponse from(Visit visit) {
        return AdminVisitListResponse.builder()
                .id(visit.getId().getValue())
                .date(visit.getDate())
                .startedAt(visit.getStartedAt())
                .endedAt(visit.getEndedAt())
                .place(visit.getPlace())
                .expense(visit.getExpense())
                .memberCount(visit.getVisitMembers().size())
                .photoCount(visit.getPhotoCount())
                .createdAt(visit.getCreatedAt())
                .build();
    }

    public static List<AdminVisitListResponse> from(List<Visit> visits) {
        return visits.stream()
                .map(AdminVisitListResponse::from)
                .collect(Collectors.toList());
    }
}

