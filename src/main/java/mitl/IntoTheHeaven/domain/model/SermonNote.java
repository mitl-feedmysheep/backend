package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class SermonNote extends AggregateRoot<SermonNote, SermonNoteId> {

    private final MemberId memberId;
    private final String title;
    private final LocalDate sermonDate;
    private final String preacher;
    private final String serviceType;
    private final String scripture;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    public SermonNote delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public SermonNote update(String title, LocalDate sermonDate, String preacher,
                             String serviceType, String scripture, String content) {
        return this.toBuilder()
                .title(title)
                .sermonDate(sermonDate)
                .preacher(preacher)
                .serviceType(serviceType)
                .scripture(scripture)
                .content(content)
                .build();
    }
}
