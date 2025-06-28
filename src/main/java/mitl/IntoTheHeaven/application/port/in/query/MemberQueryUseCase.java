package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.UUID;

public interface MemberQueryUseCase {
    Member getMemberById(MemberId memberId);
    List<Member> getMembersByGroupId(UUID groupId);
} 