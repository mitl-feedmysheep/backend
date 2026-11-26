package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.Prayer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MyPrayerResponse(
        UUID id,
        String prayerRequest,
        String description,
        boolean isAnswered,
        LocalDateTime createdAt,
        String groupName,
        LocalDate gatheringDate,
        /** MY: 나의 기도(직접 작성), GATHERING: 모임 기도제목 */
        String source
) {
    public static MyPrayerResponse from(Prayer prayer) {
        return MyPrayerResponse.builder()
                .id(prayer.getId().getValue())
                .prayerRequest(prayer.getPrayerRequest())
                .description(prayer.getDescription())
                .isAnswered(prayer.isAnswered())
                .createdAt(prayer.getCreatedAt())
                .groupName(prayer.getGroupName())
                .gatheringDate(prayer.getGatheringDate())
                .source(prayer.isPersonal() ? "MY" : "GATHERING")
                .build();
    }

    public static List<MyPrayerResponse> from(List<Prayer> prayers) {
        return prayers.stream()
                .map(MyPrayerResponse::from)
                .toList();
    }
}
