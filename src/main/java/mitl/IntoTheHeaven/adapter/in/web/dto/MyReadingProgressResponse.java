package mitl.IntoTheHeaven.adapter.in.web.dto;

import mitl.IntoTheHeaven.application.port.in.query.ReadingPlanQueryUseCase.MyReadingProgress;

import java.time.LocalDate;
import java.util.List;

public record MyReadingProgressResponse(
        int completedCount,
        int totalDays,
        int progressPercent,
        int streak,
        List<LocalDate> completedDates,
        List<LocalDate> scheduledDates
) {
    public static MyReadingProgressResponse from(MyReadingProgress progress) {
        return new MyReadingProgressResponse(
                progress.completedCount(),
                progress.totalDays(),
                progress.progressPercent(),
                progress.streak(),
                progress.completedDates(),
                progress.scheduledDates()
        );
    }
}
