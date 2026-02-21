package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.Sex;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class BirthdayMemberResponse {
    private final UUID memberId;
    private final String name;
    private final LocalDate birthday;
    private final String sex;

    @Builder
    public BirthdayMemberResponse(UUID memberId, String name, LocalDate birthday, Sex sex) {
        this.memberId = memberId;
        this.name = name;
        this.birthday = birthday;
        this.sex = sex != null ? sex.getValue() : null;
    }
}
