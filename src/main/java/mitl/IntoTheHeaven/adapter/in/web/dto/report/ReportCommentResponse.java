package mitl.IntoTheHeaven.adapter.in.web.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.ReportComment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ReportCommentResponse {

    private final UUID id;
    private final UUID authorId;
    private final String authorName;
    private final String content;
    @JsonProperty("isMine")
    private final boolean isMine;
    private final LocalDateTime createdAt;

    public static ReportCommentResponse from(ReportComment comment, MemberId callerId) {
        return ReportCommentResponse.builder()
                .id(comment.getId().getValue())
                .authorId(comment.getAuthorId().getValue())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .isMine(comment.getAuthorId().equals(callerId))
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static List<ReportCommentResponse> from(List<ReportComment> comments, MemberId callerId) {
        return comments.stream()
                .map(comment -> ReportCommentResponse.from(comment, callerId))
                .toList();
    }
}
