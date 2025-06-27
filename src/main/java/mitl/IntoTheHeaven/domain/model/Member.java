package mitl.IntoTheHeaven.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

@Getter
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

    @Builder
    public Member(MemberId id, String name, String email, String password, Gender sex, LocalDate birthday, String phone, String profileUrl, String address, String description, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.sex = sex;
        this.birthday = birthday;
        this.phone = phone;
        this.profileUrl = profileUrl;
        this.address = address;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
} 