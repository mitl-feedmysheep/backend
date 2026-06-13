package mitl.IntoTheHeaven.adapter.in.web.dto;

import mitl.IntoTheHeaven.domain.model.ReadingPlanDay;

import java.util.List;
import java.util.UUID;

public record TodayReadingResponse(
        UUID dayId,
        int dayNumber,
        String planTitle,
        String readingRange,
        String audioUrl,
        String videoUrl,
        String description,
        List<MediaInfo> medias,
        boolean completed
) {
    public record MediaInfo(String url, String mediaType) {}

    public static TodayReadingResponse from(ReadingPlanDay day, boolean completed, String planTitle) {
        List<MediaInfo> medias = day.getMedias().stream()
                .map(m -> new MediaInfo(m.getUrl(), m.getMediaType().name()))
                .toList();
        return new TodayReadingResponse(
                day.getId().getValue(),
                day.getDayNumber(),
                planTitle,
                day.getReadingRange(),
                day.getAudioUrl(),
                day.getVideoUrl(),
                day.getDescription(),
                medias,
                completed
        );
    }
}
