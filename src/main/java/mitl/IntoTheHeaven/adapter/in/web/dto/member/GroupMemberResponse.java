package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.GroupMember;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class GroupMemberResponse {

    private final UUID id;
    private final String name;
    private final String email;
    private final Sex sex;
    private final LocalDate birthday;
    private final String phone;
    private final String profileUrl;
    private final GroupMemberRole role;

    public static GroupMemberResponse from(GroupMember groupMember) {
        return GroupMemberResponse.builder()
                .id(groupMember.getId().getValue())
                .name(groupMember.getMember().getName())
                .email(groupMember.getMember().getEmail())
                .sex(groupMember.getMember().getSex())
                .birthday(groupMember.getMember().getBirthday())
                .phone(groupMember.getMember().getPhone())
                .profileUrl(groupMember.getMember().getProfileUrl())
                .role(groupMember.getRole())
                .build();
    }

    public static List<GroupMemberResponse> from(List<GroupMember> groupMembers) {
        return groupMembers.stream()
                .map(GroupMemberResponse::from)
                .collect(Collectors.toList());
    }
}