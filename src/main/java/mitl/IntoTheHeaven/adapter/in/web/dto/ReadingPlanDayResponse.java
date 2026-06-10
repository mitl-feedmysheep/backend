package mitl.IntoTheHeaven.adapter.in.web.dto;

import mitl.IntoTheHeaven.domain.model.ReadingPlanDay;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ReadingPlanDayResponse(
        UUID dayId,
        LocalDate readingDate,
        int dayNumber,
        String readingRange,
        String youtubeUrl
) {
    public static ReadingPlanDayResponse from(ReadingPlanDay day) {
        return new ReadingPlanDayResponse(
                day.getId().getValue(),
                day.getReadingDate(),
                day.getDayNumber(),
                day.getReadingRange(),
                day.getYoutubeUrl()
        );
    }

    public static List<ReadingPlanDayResponse> from(List<ReadingPlanDay> days) {
        return days.stream().map(ReadingPlanDayResponse::from).toList();
    }
}
