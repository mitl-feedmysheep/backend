package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class Group extends AggregateRoot<Group, GroupId> {

    private final String name;
    private final String description;
    private final ChurchId churchId;
    private final LocalDate startDate;
    private final LocalDate endDate;
} 