package mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.SermonNote;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record SermonNoteResponse(
        UUID id,
        String title,
        LocalDate sermonDate,
        String preacher,
        String serviceType,
        String scripture,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SermonNoteResponse from(SermonNote sermonNote) {
        return SermonNoteResponse.builder()
                .id(sermonNote.getId().getValue())
                .title(sermonNote.getTitle())
                .sermonDate(sermonNote.getSermonDate())
                .preacher(sermonNote.getPreacher())
                .serviceType(sermonNote.getServiceType())
                .scripture(sermonNote.getScripture())
                .content(sermonNote.getContent())
                .createdAt(sermonNote.getCreatedAt())
                .updatedAt(sermonNote.getUpdatedAt())
                .build();
    }

    public static List<SermonNoteResponse> from(List<SermonNote> sermonNotes) {
        return sermonNotes.stream()
                .map(SermonNoteResponse::from)
                .toList();
    }
}
