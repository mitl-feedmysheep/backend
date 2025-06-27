package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;

@Getter
public class Group extends AggregateRoot<Group, GroupId> {

    private String name;
    private String description;
    private ChurchId churchId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public Group(GroupId id, String name, String description, ChurchId churchId, LocalDate startDate, LocalDate endDate) {
        super(id);
        this.name = name;
        this.description = description;
        this.churchId = churchId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
} 