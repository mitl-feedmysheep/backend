package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.Visit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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
                List<MediaResponse> medias,
                LocalDateTime createdAt) {
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
                                                // Sort by birthday ascending (oldest first)
                                                .sorted(Comparator.comparing(vm -> vm.getChurchMember() != null
                                                                && vm.getChurchMember().getMember() != null
                                                                && vm.getChurchMember().getMember()
                                                                                .getBirthday() != null
                                                                                                ? vm.getChurchMember()
                                                                                                                .getMember()
                                                                                                                .getBirthday()
                                                                                                : LocalDate.MAX))
                                                .map(VisitMemberResponse::from)
                                                .collect(Collectors.toList()))
                                .medias(visit.getMedias().stream()
                                                .map(MediaResponse::from)
                                                .collect(Collectors.toList()))
                                .createdAt(visit.getCreatedAt())
                                .build();
        }
}
