package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Member;

import java.util.UUID;

public interface MemberQueryUseCase {
    Member findMemberById(UUID memberId);
} 