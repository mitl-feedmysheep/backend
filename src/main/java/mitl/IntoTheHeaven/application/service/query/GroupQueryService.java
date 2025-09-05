package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupQueryService implements GroupQueryUseCase {

    private final GroupPort groupPort;

    @Override
    public List<Group> getGroupsByMemberId(MemberId memberId) {
        return groupPort.findGroupsByMemberId(memberId.getValue());
    }

    @Override
    public List<Group> getGroupsByMemberIdAndChurchId(MemberId memberId, ChurchId churchId) {
        return groupPort.findGroupsByMemberIdAndChurchId(memberId.getValue(), churchId.getValue());
    }

    @Override
    public List<GroupMember> getGroupMembersByGroupId(UUID groupId) {
        List<GroupMember> groupMembers = groupPort.findGroupMembersByGroupId(groupId);
        return groupMembers.stream()
                .sorted(
                        Comparator
                                .comparingInt((GroupMember gm) -> {
                                    GroupMemberRole role = gm.getRole();
                                    if (role == GroupMemberRole.LEADER) return 0;
                                    if (role == GroupMemberRole.SUB_LEADER) return 1;
                                    return 2; // MEMBER and others
                                })
                                .thenComparing(gm -> gm.getMember().getBirthday())
                )
                .toList();
    }

    @Override
    public GroupMember getGroupMemberByGroupIdAndMemberId(GroupId groupId, MemberId memberId) {
        return groupPort.findGroupMemberByGroupIdAndMemberId(groupId.getValue(), memberId.getValue());
    }
}