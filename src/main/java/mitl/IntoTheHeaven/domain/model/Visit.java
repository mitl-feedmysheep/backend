package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public class Visit extends AggregateRoot<Visit, VisitId> {

    private final ChurchId churchId;
    private final Church church;
    private final ChurchMemberId pastorMemberId;
    private final LocalDate date;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final String place;
    private final Integer expense;
    private final String notes;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    @Builder.Default
    private final List<VisitMember> visitMembers = new ArrayList<>();

    @Builder.Default
    private final List<Media> media = new ArrayList<>();

    /**
     * Delete visit (soft delete)
     */
    public Visit delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Check if visit has photos
     */
    public boolean hasPhotos() {
        return !media.isEmpty();
    }
}
