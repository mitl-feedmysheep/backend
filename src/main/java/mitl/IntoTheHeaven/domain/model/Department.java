package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

@Getter
@SuperBuilder
public class Department extends AggregateRoot<Department, DepartmentId> {

    private final String name;
    private final String description;
    private final ChurchId churchId;
    private final boolean isDefault;
}
