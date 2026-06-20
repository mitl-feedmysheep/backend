package mitl.IntoTheHeaven.adapter.in.web.dto;

import mitl.IntoTheHeaven.domain.model.ReadingPlanDay;

import java.util.List;
import java.util.UUID;

public record ReadingPlanDayResponse(
        UUID dayId,
        int dayNumber,
        String readingRange,
        String audioUrl,
        String videoUrl
) {
    public static ReadingPlanDayResponse from(ReadingPlanDay day) {
        return new ReadingPlanDayResponse(
                day.getId().getValue(),
                day.getDayNumber(),
                day.getReadingRange(),
                day.getAudioUrl(),
                day.getVideoUrl()
        );
    }

    public static List<ReadingPlanDayResponse> from(List<ReadingPlanDay> days) {
        return days.stream().map(ReadingPlanDayResponse::from).toList();
    }
}
