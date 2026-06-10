package mitl.IntoTheHeaven.adapter.in.web.dto;

import mitl.IntoTheHeaven.domain.model.ReadingPlanDay;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TodayReadingResponse(
        UUID dayId,
        LocalDate readingDate,
        int dayNumber,
        String readingRange,
        String youtubeUrl,
        String description,
        List<MediaInfo> medias,
        boolean completed
) {
    public record MediaInfo(String url, String mediaType) {}

    public static TodayReadingResponse from(ReadingPlanDay day, boolean completed) {
        List<MediaInfo> medias = day.getMedias().stream()
                .map(m -> new MediaInfo(m.getUrl(), m.getMediaType().name()))
                .toList();
        return new TodayReadingResponse(
                day.getId().getValue(),
                day.getReadingDate(),
                day.getDayNumber(),
                day.getReadingRange(),
                day.getYoutubeUrl(),
                day.getDescription(),
                medias,
                completed
        );
    }
}
