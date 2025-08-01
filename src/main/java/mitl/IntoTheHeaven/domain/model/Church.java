package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class Church extends AggregateRoot<Church, ChurchId> {

    private final String name;
    private final String location;
    private final String number;
    private final String homepageUrl;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;
}