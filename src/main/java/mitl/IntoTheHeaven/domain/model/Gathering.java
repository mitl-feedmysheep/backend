package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;

@Getter
public class Gathering extends AggregateRoot<Gathering, GatheringId> {

    private final GroupId groupId;
    private String name;
    private LocalDate date;
    private String place;

    @Builder
    public Gathering(GatheringId id, GroupId groupId, String name, LocalDate date, String place) {
        super(id);
        this.groupId = groupId;
        this.name = name;
        this.date = date;
        this.place = place;
    }
} 