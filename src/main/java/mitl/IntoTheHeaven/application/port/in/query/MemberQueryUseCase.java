package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Member;

import java.util.List;
import java.util.UUID;

public interface MemberQueryUseCase {
    Member getMemberById(UUID memberId);
    List<Member> getMembersByGroupId(UUID groupId);
} 