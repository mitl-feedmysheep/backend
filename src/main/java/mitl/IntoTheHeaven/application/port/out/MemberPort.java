package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberPort {

  List<Member> findMembersByGroupId(UUID groupId);

  List<GroupMember> findGroupMembersByGroupId(UUID groupId);

  Optional<Member> findById(UUID memberId);

  Member save(Member member);

  Optional<Member> findByEmail(String email);
} 