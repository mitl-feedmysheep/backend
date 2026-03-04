package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.domain.enums.BaptismStatus;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.Sex;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MemberSearchResponse {

    private final String memberId;
    private final String name;
    private final Sex sex;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate birthday;

    private final List<GroupInfo> groups;

    // 민감 필드 - 리더 이상에게만 제공, 그 외 null
    private final String phone;
    private final String address;

    private final String occupation;
    private final BaptismStatus baptismStatus;
    private final String mbti;
    private final String description;

    @Getter
    @Builder
    public static class GroupInfo {
        private final String groupId;
        private final String groupName;
        private final GroupMemberRole role;

        public static GroupInfo from(MemberWithGroups.GroupInfo groupInfo) {
            return GroupInfo.builder()
                    .groupId(groupInfo.getGroupId().toString())
                    .groupName(groupInfo.getGroupName())
                    .role(groupInfo.getRole())
                    .build();
        }
    }

    public static MemberSearchResponse from(MemberWithGroups mwg, boolean isLeader) {
        List<GroupInfo> groups = mwg.getGroups() != null
                ? mwg.getGroups().stream()
                        .map(GroupInfo::from)
                        .toList()
                : List.of();

        return MemberSearchResponse.builder()
                .memberId(mwg.getId().getValue().toString())
                .name(mwg.getName())
                .sex(mwg.getSex())
                .birthday(mwg.getBirthday())
                .groups(groups)
                .phone(isLeader ? mwg.getPhone() : null)
                .address(isLeader ? mwg.getAddress() : null)
                .occupation(isLeader ? mwg.getOccupation() : null)
                .baptismStatus(isLeader ? mwg.getBaptismStatus() : null)
                .mbti(isLeader ? mwg.getMbti() : null)
                .description(mwg.getDescription())
                .build();
    }

    public static List<MemberSearchResponse> from(List<MemberWithGroups> list, boolean isLeader) {
        return list.stream()
                .map(mwg -> MemberSearchResponse.from(mwg, isLeader))
                .toList();
    }
}
