package mitl.IntoTheHeaven.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.BaptismStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

@Getter
@SuperBuilder(toBuilder = true)
public class Member extends AggregateRoot<Member, MemberId> {

    private final String name;
    private final String email;
    private final String password;
    private final Sex sex;
    private final LocalDate birthday;
    private final String phone;
    private final String profileUrl;
    private final String address;
    private final String description;
    private final String occupation;
    private final BaptismStatus baptismStatus;
    private final String mbti;
    private final Boolean isProvisioned;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

} 