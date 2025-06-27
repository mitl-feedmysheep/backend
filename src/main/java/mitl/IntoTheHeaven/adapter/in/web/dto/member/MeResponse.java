package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.domain.model.Member;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class MeResponse {

    private final UUID id;
    private final String name;
    private final String email;
    private final Gender sex;
    private final LocalDate birthday;
    private final String phone;
    private final String profileUrl;

    public static MeResponse from(Member member) {
        return MeResponse.builder()
                .id(member.getId().getValue())
                .name(member.getName())
                .email(member.getEmail())
                .sex(member.getSex())
                .birthday(member.getBirthday())
                .phone(member.getPhone())
                .profileUrl(member.getProfileUrl())
                .build();
    }
} 