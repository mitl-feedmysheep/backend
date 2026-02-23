package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.BaptismStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class MeResponse {

    private final UUID id;
    private final String name;
    private final String email;
    private final Sex sex;
    private final LocalDate birthday;
    private final String phone;
    private final String profileUrl;
    private final String address;
    private final String occupation;
    private final BaptismStatus baptismStatus;
    private final String mbti;

    public static MeResponse from(Member member) {
        return MeResponse.builder()
                .id(member.getId().getValue())
                .name(member.getName())
                .email(member.getEmail())
                .sex(member.getSex())
                .birthday(member.getBirthday())
                .phone(member.getPhone())
                .profileUrl(member.getProfileUrl())
                .address(member.getAddress())
                .occupation(member.getOccupation())
                .baptismStatus(member.getBaptismStatus())
                .mbti(member.getMbti())
                .build();
    }

    public static List<MeResponse> from(List<Member> members) {
        return members.stream()
                .map(MeResponse::from)
                .collect(Collectors.toList());
    }
} 