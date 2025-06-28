package mitl.IntoTheHeaven.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

@Getter
@SuperBuilder
public class Member extends AggregateRoot<Member, MemberId> {

    private final String name;
    private final String email;
    private final String password;
    private final Gender sex;
    private final LocalDate birthday;
    private final String phone;
    private final String profileUrl;
    private final String address;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;
} 