package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.GroupMemberQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupMemberQueryService implements GroupMemberQueryUseCase {

    private final GroupPort groupPort;

    @Override
    public GroupMember getGroupMemberByGroupIdAndMemberId(GroupId groupId, MemberId memberId) {
        return groupPort.findGroupMemberByGroupIdAndMemberId(groupId.getValue(), memberId.getValue());
    }
}