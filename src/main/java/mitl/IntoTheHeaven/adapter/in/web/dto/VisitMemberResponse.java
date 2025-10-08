package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.VisitMember;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record VisitMemberResponse(
                UUID id,
                UUID churchMemberId,
                String memberName,
                String story,
                List<PrayerResponse> prayers) {
        public static VisitMemberResponse from(VisitMember visitMember) {
                return VisitMemberResponse.builder()
                                .id(visitMember.getId().getValue())
                                .churchMemberId(visitMember.getChurchMemberId().getValue())
                                .memberName(visitMember.getChurchMember() != null
                                                && visitMember.getChurchMember().getMember() != null
                                                                ? visitMember.getChurchMember().getMember().getName()
                                                                : null)
                                .story(visitMember.getStory())
                                .prayers(visitMember.getPrayers().stream()
                                                .map(PrayerResponse::from)
                                                .collect(Collectors.toList()))
                                .build();
        }
}
