package mitl.IntoTheHeaven.adapter.in.web.dto;

import mitl.IntoTheHeaven.application.port.in.query.ReadingPlanQueryUseCase.MemberReadingProgress;

import java.util.List;
import java.util.UUID;

public record DepartmentReadingProgressResponse(
        UUID memberId,
        String memberName,
        int completedCount,
        int totalDays,
        int progressPercent
) {
    public static DepartmentReadingProgressResponse from(MemberReadingProgress p) {
        return new DepartmentReadingProgressResponse(
                p.memberId(), p.memberName(), p.completedCount(), p.totalDays(), p.progressPercent()
        );
    }

    public static List<DepartmentReadingProgressResponse> from(List<MemberReadingProgress> list) {
        return list.stream().map(DepartmentReadingProgressResponse::from).toList();
    }
}
