package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.Sex;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AdminMemberSearchResponse {

    private final String memberId;
    private final String name;
    private final String email;
    private final String phone;
    private final Sex sex;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate birthday;

    private final String address;
    private final String description;
    private final List<AdminGroupInfo> groups;

    @Getter
    @Builder
    public static class AdminGroupInfo {
        private final String groupId;
        private final String groupName;
        private final GroupMemberRole role;

        public static AdminGroupInfo from(MemberWithGroups.GroupInfo groupInfo) {
            return AdminGroupInfo.builder()
                    .groupId(groupInfo.getGroupId().toString())
                    .groupName(groupInfo.getGroupName())
                    .role(groupInfo.getRole())
                    .build();
        }
    }

    public static AdminMemberSearchResponse from(MemberWithGroups memberWithGroups) {
        List<AdminGroupInfo> groups = memberWithGroups.getGroups() != null
                ? memberWithGroups.getGroups().stream()
                        .map(AdminGroupInfo::from)
                        .toList()
                : List.of();

        return AdminMemberSearchResponse.builder()
                .memberId(memberWithGroups.getId().getValue().toString())
                .name(memberWithGroups.getName())
                .email(memberWithGroups.getEmail())
                .phone(memberWithGroups.getPhone())
                .sex(memberWithGroups.getSex())
                .birthday(memberWithGroups.getBirthday())
                .address(memberWithGroups.getAddress())
                .description(memberWithGroups.getDescription())
                .groups(groups)
                .build();
    }

    public static List<AdminMemberSearchResponse> from(List<MemberWithGroups> membersWithGroups) {
        return membersWithGroups.stream()
                .map(AdminMemberSearchResponse::from)
                .toList();
    }
}
