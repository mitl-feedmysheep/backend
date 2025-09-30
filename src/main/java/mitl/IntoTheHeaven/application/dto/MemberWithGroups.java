package mitl.IntoTheHeaven.application.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MemberWithGroups {

    private final MemberId id;
    private final String name;
    private final String email;
    private final Sex sex;
    private final LocalDate birthday;
    private final String phone;
    private final String address;
    private final String description;
    private final List<GroupInfo> groups;

    @Getter
    @Builder
    public static class GroupInfo {
        private final UUID groupId;
        private final String groupName;
        private final GroupMemberRole role;
    }

    /**
     * Member 도메인으로 변환 (GroupMember 정보는 제외)
     */
    public Member toMember() {
        return Member.builder()
                .id(id)
                .name(name)
                .email(email)
                .sex(sex)
                .birthday(birthday)
                .phone(phone)
                .address(address)
                .build();
    }
}
